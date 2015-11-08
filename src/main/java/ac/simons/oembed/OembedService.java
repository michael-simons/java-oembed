/*
 * Copyright 2014 michael-simons.eu.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ac.simons.oembed;

import ac.simons.oembed.OembedResponse.Format;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;
import java.util.function.Function;
import java.util.stream.Collectors;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Ehcache;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import org.joor.Reflect;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Entities.EscapeMode;
import org.jsoup.parser.Parser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Michael J. Simons, 2014-12-31
 */
public class OembedService {

    public static final Logger logger = LoggerFactory.getLogger(OembedService.class.getPackage().getName());

    /**
     * This is the http client that will execute all requests.
     */
    private final HttpClient httpClient;

    /**
     * An optional cache manager used for caching oembed responses.
     */
    private final Optional<CacheManager> cacheManager;

    /**
     * The user agent to use. We want to be a goot net citizen and provide some
     * info about us.
     */
    private final String userAgent;

    /**
     * An optional application name;
     */
    private Optional<String> applicationName;

    /**
     * The available parsers. This list isn't changeable.
     */
    private final Map<Format, OembedParser> parsers;

    /**
     * All configured endpoints.
     */
    private final Map<OembedEndpoint, RequestProvider> endpoints;

    /**
     * All configured renderers. The handlers are grouped by URL schemes.
     */
    private final Map<List<String>, OembedResponseRenderer> renderers;

    /**
     * A flag wether autodiscovery of oembed endpoints should be tried. Defaults
     * to false.
     */
    private boolean autodiscovery = false;

    /**
     * The name of the cached used by this service. Defaults to the services
     * fully qualified class name.
     */
    private String cacheName = OembedService.class.getName();

    /**
     * Time in seconds responses are cached. Used if the response has no
     * cache_age.
     */
    private long defaultCacheAge = 3600;

    /**
     * Used for autodiscovered endpoints.
     */
    private final RequestProvider defaultRequestProvider = new DefaultRequestProvider();

    /**
     * Used for autodiscovered endpoints.
     */
    private final OembedResponseRenderer defaultRenderer = new DefaultOembedResponseRenderer();

    /**
     * Creates a new {@code OembedService}. This service depends on a
     * {@link HttpClient} and can use a {@link CacheManager} for caching
     * requests.
     *
     * @param httpClient Mandatory http client
     * @param cacheManager Optional cache manager
     * @param endpoints The static endpoints
     * @param applicationName Optional application name
     */
    public OembedService(final HttpClient httpClient, final CacheManager cacheManager, final List<OembedEndpoint> endpoints, String applicationName) {
	this.httpClient = httpClient;
	this.cacheManager = Optional.ofNullable(cacheManager);
	final Properties version = new Properties();
	try {
	    version.load(OembedService.class.getResourceAsStream("/oembed.properties"));
	} catch (IOException e) {
	}
	this.userAgent = String.format("Java/%s java-oembed2/%s", System.getProperty("java.version"), version.getProperty("de.dailyfratze.text.oembed.version"));
	this.applicationName = Optional.ofNullable(applicationName);

	final Map<Format, OembedParser> hlp = new HashMap<>();
	hlp.put(Format.json, new OembedJsonParser());
	hlp.put(Format.xml, new OembedXmlParser());
	this.parsers = Collections.unmodifiableMap(hlp);

	this.endpoints = endpoints.stream().collect(Collectors.toMap(Function.identity(), endpoint -> {
	    logger.debug("Endpoint {} will match the following patterns: {}", endpoint.getName(), endpoint.getUrlSchemes());
	    logger.debug("Configuring request provider of type {} for endpoint {}...", endpoint.getRequestProviderClass(), endpoint.getName());
	    logger.debug("Using properties: {}", endpoint.getRequestProviderProperties());	    
	    final Reflect requestProvider = Reflect.on(endpoint.getRequestProviderClass()).create();	    
	    Optional.ofNullable(endpoint.getRequestProviderProperties()).ifPresent(p -> p.forEach((k, v) -> requestProvider.call(k, v)));
	    return requestProvider.<RequestProvider>get();	
	}));

	this.renderers = endpoints.stream().collect(Collectors.toMap(OembedEndpoint::getUrlSchemes, endpoint -> {
	    logger.debug("Configuring response renderer of type {} for endpoint {}...", endpoint.getResponseRendererClass(), endpoint.getName());
	    logger.debug("Using properties: {}", endpoint.getResponseRendererProperties());

	    final Reflect oembedResponseRenderer = Reflect.on(endpoint.getResponseRendererClass()).create();
	    Optional.ofNullable(endpoint.getResponseRendererProperties()).ifPresent(p -> p.forEach((k, v) -> oembedResponseRenderer.set(k, v)));
	    return oembedResponseRenderer.get();	    
	}));

	logger.debug("Oembed has {} endpoints and autodiscovery {} enabled...", this.endpoints.size(), this.autodiscovery ? "is" : "is not");
	logger.info("Oembed ({}) ready...", this.userAgent);
    }

    /**
     * @return the current configuration of oembed autodiscovery
     */
    public boolean isAutodiscovery() {
	return autodiscovery;
    }

    /**
     * Updates to configuration of oembed autodiscovery.
     *
     * @param autodiscovery New flag wether oembed endpoints should be
     * autodiscovered
     */
    public void setAutodiscovery(boolean autodiscovery) {
	this.autodiscovery = autodiscovery;
    }

    /**
     * @return The name of the cached used by this service
     */
    public String getCacheName() {
	return cacheName;
    }

    /**
     * Changes the name of the cache used. If a cache manager is present, it
     * clears the old cache and removes it.
     *
     * @param cacheName The new cache name
     */
    public void setCacheName(String cacheName) {
	if (this.cacheManager.isPresent() && this.cacheManager.get().cacheExists(this.cacheName)) {
	    this.cacheManager.get().removeCache(this.cacheName);
	}
	this.cacheName = cacheName;
    }

    /**
     * @return The default time in seconds responses are cached.
     */
    public long getDefaultCacheAge() {
	return defaultCacheAge;
    }

    /**
     * Changes the default cache age.
     *
     * @param defaultCacheAge New default cache age in seconds
     */
    public void setDefaultCacheAge(long defaultCacheAge) {
	this.defaultCacheAge = defaultCacheAge;
    }

    /**
     * Tries to find an endpoint for the given url. It first tries to find an
     * endpoint within the configured endpoints by a matching url scheme. If
     * that results in an empty endpoint and autodiscovier is true, than an http
     * get request is made to the given url, checking for alternate links with
     * the type {@code application/(json|xml)+oembed}.
     *
     * @param url URL that should be embedded
     * @return An optional endpoint for this url
     */
    Optional<OembedEndpoint> findEndpointFor(final String url) {
	Optional<OembedEndpoint> rv = this.endpoints.keySet().stream()
		.filter(
			endpoint -> endpoint
			.getUrlSchemes()
			.stream()
			.map(String::trim)
			.anyMatch(url::matches)
		)
		.findFirst();
	if (!rv.isPresent() && autodiscovery) {
	    try {
		final HttpResponse httpResponse = this.httpClient.execute(new HttpGet(url));
		if (httpResponse.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
		    logger.warn("Autodiscovery for {} failed, server returned error {}: {}", url, httpResponse.getStatusLine().getStatusCode(), EntityUtils.toString(httpResponse.getEntity()));
		} else {
		    final Document document = Jsoup.parse(EntityUtils.toString(httpResponse.getEntity(), "UTF-8"), url);
		    rv = document.getElementsByAttributeValue("rel", "alternate").stream().map(alternate -> {
			OembedEndpoint autodiscoveredEndpoint = null;
			try {
			    if (alternate.attr("type").equalsIgnoreCase("application/json+oembed")) {
				autodiscoveredEndpoint = new AutodiscoveredOembedEndpoint(new URI(alternate.absUrl("href")), Format.json);
			    } else if (alternate.attr("type").equalsIgnoreCase("text/xml+oembed")) {
				autodiscoveredEndpoint = new AutodiscoveredOembedEndpoint(new URI(alternate.absUrl("href")), Format.xml);
			    }
			} catch (URISyntaxException ex) {
			    // Just ignore them
			}
			return autodiscoveredEndpoint;
		    }).filter(Objects::nonNull).findFirst();
		}
	    } catch (IOException e) {
		logger.warn("Autodiscovery for {} failed: {}", url, e.getMessage());
	    }
	}
	return rv;
    }

    /**
     * Executes the given HttpRequest {@code request} and returns an input
     * stream for the reponses content if no error occured and the server
     * returned a status code OK.
     *
     * @param request
     * @return
     */
    InputStream executeRequest(final HttpGet request) {
	InputStream rv = null;
	try {
	    final HttpResponse httpResponse = this.httpClient.execute(request);
	    if (httpResponse.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
		logger.warn("Skipping '{}', server returned error {}: {}", request.getURI().toString(), httpResponse.getStatusLine().getStatusCode(), EntityUtils.toString(httpResponse.getEntity()));
	    } else {
		rv = httpResponse.getEntity().getContent();
	    }
	} catch (IOException ex) {
	    logger.warn("Skipping '{}', could not get a response: {}", request.getURI().toString(), ex.getMessage());
	}
	return rv;
    }

    /**
     * Tries to find an {@link OembedResponse} for the URL {@code url}. If a
     * cache manager is present, it tries that first. If an
     * {@code OembedResponse} can be discovered and a cache manager is present,
     * that response will be cached.
     *
     * @param url The URL that might be represented by oembed.
     * @return An oembed response
     */
    Optional<OembedResponse> getOembedResponseFor(final String url) {
	String trimmedUrl;
	if (url == null || (trimmedUrl = url.trim()).isEmpty()) {
	    logger.debug("Ignoring empty url...");
	    return Optional.empty();
	}

	Optional<OembedResponse> rv = this.cacheManager
		.map(cacheManager -> cacheManager.addCacheIfAbsent(this.cacheName).get(trimmedUrl))
		.map(element -> (OembedResponse) element.getObjectValue());
	// If there's already an oembed response cached, use that
	if (rv.isPresent()) {
	    logger.debug("Using OembedResponse from cache for '{}'...", trimmedUrl);
	    return rv;
	}

	final Optional<OembedEndpoint> endPoint = this.findEndpointFor(trimmedUrl);
	logger.debug("Found endpoint {} for '{}'...", endPoint, trimmedUrl);
	rv = endPoint
		.map(ep -> this.endpoints
			.getOrDefault(ep, defaultRequestProvider)
			.createRequestFor(this.userAgent, this.applicationName, ep.toApiUrl(trimmedUrl))
		)
		.map(this::executeRequest)
		.map(content -> {
		    OembedResponse oembedResponse = null;
		    try {
			oembedResponse = parsers.get(endPoint.get().getFormat()).unmarshal(content);
		    } catch (OembedException ex) {
			logger.warn("Server returned an invalid oembed format for url '{}': {}", trimmedUrl, ex.getMessage());
		    }
		    return oembedResponse;
		});

	if (this.cacheManager.isPresent()) {
	    final Ehcache cache = this.cacheManager.get().addCacheIfAbsent(this.cacheName);
	    // Cache at least 60 seconds
	    final int cacheAge = (int) Math.min(Math.max(60l, rv.map(OembedResponse::getCacheAge).orElse(this.defaultCacheAge)), Integer.MAX_VALUE);
	    // We're adding failed urls to the cache as well to prevent them
	    // from being tried again over and over (at least for some seconds)
	    cache.put(new net.sf.ehcache.Element(trimmedUrl, rv.orElse(null), null, null, cacheAge));
	    logger.debug("Cached {} for {} seconds for url '{}'...", rv, cacheAge, trimmedUrl);
	}

	return rv;
    }
    
    /**
     * @see #embedUrls(java.lang.String, java.util.Optional, java.lang.Class) 
     * @param textWithEmbeddableUrls
     * @param baseUrl
     * @return 
     */
    public String embedUrls(final String textWithEmbeddableUrls, final Optional<String> baseUrl) {
	return embedUrls(textWithEmbeddableUrls, baseUrl, String.class);
    }

    /**
     * Scans the text {@code textWithEmbeddableUrls} for anchor tags and tries
     * to find {@link OembedEndpoint} for those urls. If such an endpoint
     * exists, it tries to get an {@link OembedResponse} of that url from the
     * endpoint. This response will then be rendered as html and is used to
     * replace the anchor tag.
     *
     * @param <T> Type of the resulting document with embedded links
     * @param textWithEmbeddableUrls Text that contains embeddable urls
     * @param baseUrl An optional base url for resolving relative urls
     * @param targetClass
     * @return The same text with embedded urls if such urls existed
     */
    public <T> T embedUrls(final String textWithEmbeddableUrls, final Optional<String> baseUrl, Class<? extends T> targetClass) {
	T rv;
	if (String.class.isAssignableFrom(targetClass)) {
	    rv = (T) textWithEmbeddableUrls;
	} else if (Document.class.isAssignableFrom(targetClass)) {
	    rv = (T) Document.createShell(baseUrl.orElse(""));
	} else {
	    throw new OembedException(String.format("Invalid target class: %s", targetClass.getName()));
	}

	if (!(textWithEmbeddableUrls == null || textWithEmbeddableUrls.trim().isEmpty())) {
	    // Create a document
	    final Document document = embedUrls(Jsoup.parseBodyFragment(textWithEmbeddableUrls, baseUrl.orElse("")));
	    if(Document.class.isAssignableFrom(targetClass)) {
		rv = (T) document;
	    } else {
		document
			.outputSettings()
			.prettyPrint(false)
			.escapeMode(EscapeMode.xhtml)
			.charset(StandardCharsets.UTF_8);
		rv = (T) Parser.unescapeEntities(document.body().html().trim(), true);
	    }
	}
	return rv;
    }

    /**
     * A convenience method to embed urls in an existing document.
     *
     * @see #embedUrls(java.lang.String, java.util.Optional, java.lang.Class) 
     * @param document Existing document, will be modified
     * @return Modified document with embedded urls
     */
    public Document embedUrls(final Document document) {
	for (Element a : document.getElementsByTag("a")) {
	    final String absUrl = a.absUrl("href");
	    final Optional<String> html = this.getOembedResponseFor(absUrl).map(response -> {
		final OembedResponseRenderer renderer = this.renderers.entrySet().stream()
			.filter(entry -> entry.getKey().stream().anyMatch(absUrl::matches))
			.findFirst()
			.map(Map.Entry::getValue)
			.orElse(this.defaultRenderer);
		return renderer.render(response, a.clone());
	    });
	    if (html.isPresent() && !html.get().trim().isEmpty()) {
		a.before(html.get().trim());
		a.remove();
	    }
	}
	return document;
    }

    /**
     * Returns an instance of an {@link OembedParser} for the given
     * {@link OembedResponse.Format}.
     *
     * @param format The format for which a parser is needed
     * @return The parser if a parser for the given format exists
     */
    public OembedParser getParser(final Format format) {
	return this.parsers.get(format);
    }
}
