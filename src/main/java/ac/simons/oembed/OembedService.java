/*
 * Copyright 2014-2018 michael-simons.eu.
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

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;
import java.util.function.Function;
import java.util.stream.Collectors;

import ac.simons.oembed.OembedResponse.Format;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Ehcache;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Entities.EscapeMode;
import org.jsoup.parser.Parser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This is the main entrpy point for dealing with OEmbed discovery.
 *
 * @author Michael J. Simons
 * @since 2014-12-31
 */
public class OembedService {

	static final Logger LOGGER = LoggerFactory.getLogger(OembedService.class.getPackage().getName());

	/**
	 * This is the http client that will execute all requests.
	 */
	private final HttpClient httpClient;

	/**
	 * An optional cache manager used for caching oembed responses.
	 */
	private final Optional<CacheManager> cacheManager;

	/**
	 * The user agent to use. We want to be a goot net citizen and provide some info about
	 * us.
	 */
	private final String userAgent;

	/**
	 * An optional application name.
	 */
	private final String applicationName;

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
	 * A flag wether autodiscovery of oembed endpoints should be tried. Defaults to false.
	 */
	private boolean autodiscovery = false;

	/**
	 * The name of the cached used by this service. Defaults to the services fully
	 * qualified class name.
	 */
	private String cacheName = OembedService.class.getName();

	/**
	 * Time in seconds responses are cached. Used if the response has no cache_age.
	 */
	private long defaultCacheAge = 3600;

	/**
	 * Used for auto-discovered endpoints.
	 */
	private final RequestProvider defaultRequestProvider = new DefaultRequestProvider();

	/**
	 * Used for auto-discovered endpoints.
	 */
	private final OembedResponseRenderer defaultRenderer = new DefaultOembedResponseRenderer();

	/**
	 * Creates a new {@code OembedService}. This service depends on a {@link HttpClient}
	 * and can use a {@link CacheManager} for caching requests.
	 * @param httpClient the mandatory http client
	 * @param cacheManager an optional cache manager
	 * @param endpoints the static endpoints
	 * @param applicationName an optional application name
	 */
	public OembedService(final HttpClient httpClient, final CacheManager cacheManager,
			final List<OembedEndpoint> endpoints, final String applicationName) {
		this.httpClient = httpClient;
		this.cacheManager = Optional.ofNullable(cacheManager);
		final Properties version = new Properties();
		try {
			version.load(OembedService.class.getResourceAsStream("/oembed.properties"));
		}
		catch (IOException ignored) {
		}
		this.userAgent = String.format("Java/%s java-oembed2/%s", System.getProperty("java.version"),
				version.getProperty("de.dailyfratze.text.oembed.version"));
		this.applicationName = applicationName;

		final Map<Format, OembedParser> hlp = new EnumMap<>(Format.class);
		hlp.put(Format.json, new OembedJsonParser());
		hlp.put(Format.xml, new OembedXmlParser());
		this.parsers = Collections.unmodifiableMap(hlp);

		this.endpoints = endpoints.stream().collect(Collectors.toMap(Function.identity(), endpoint -> {
			LOGGER.debug("Endpoint {} will match the following patterns: {}", endpoint.getName(),
					endpoint.getUrlSchemes());
			LOGGER.debug("Configuring request provider of type {} for endpoint {}...",
					endpoint.getRequestProviderClass(), endpoint.getName());
			LOGGER.debug("Using properties: {}", endpoint.getRequestProviderProperties());

			RequestProvider requestProvider;
			try {
				requestProvider = endpoint.getRequestProviderClass().getDeclaredConstructor().newInstance();
				BeanUtils.populate(requestProvider, endpoint.getRequestProviderProperties());
			}
			catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException
					| InstantiationException ex) {
				// Assuming everything is neatly configured
				throw new OembedException(ex);
			}
			return requestProvider;
		}));

		this.renderers = endpoints.stream().collect(Collectors.toMap(OembedEndpoint::getUrlSchemes, endpoint -> {
			LOGGER.debug("Configuring response renderer of type {} for endpoint {}...",
					endpoint.getResponseRendererClass(), endpoint.getName());
			LOGGER.debug("Using properties: {}", endpoint.getResponseRendererProperties());

			OembedResponseRenderer oembedResponseRenderer = null;
			try {
				oembedResponseRenderer = endpoint.getResponseRendererClass().getDeclaredConstructor().newInstance();
				BeanUtils.populate(oembedResponseRenderer, endpoint.getResponseRendererProperties());
			}
			catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException
					| InstantiationException ex) {
				// Assuming everything is neatly configured
				throw new OembedException(ex);
			}
			return oembedResponseRenderer;
		}));

		LOGGER.debug("Oembed has {} endpoints and autodiscovery {} enabled...", this.endpoints.size(),
				this.autodiscovery ? "is" : "is not");
		LOGGER.info("Oembed ({}) ready...", this.userAgent);
	}

	/**
	 * {@return the current configuration of oembed autodiscovery}
	 */
	public boolean isAutodiscovery() {
		return this.autodiscovery;
	}

	/**
	 * Updates to configuration of oembed autodiscovery.
	 * @param autodiscovery new flag whether oembed endpoints should be auto-discovered
	 */
	public void setAutodiscovery(final boolean autodiscovery) {
		this.autodiscovery = autodiscovery;
	}

	/**
	 * {@return the name of the cached used by this service}
	 */
	public String getCacheName() {
		return this.cacheName;
	}

	/**
	 * Changes the name of the cache used. If a cache manager is present, it clears the
	 * old cache and removes it.
	 * @param cacheName the new cache name
	 */
	public void setCacheName(final String cacheName) {
		if (this.cacheManager.isPresent() && this.cacheManager.get().cacheExists(this.cacheName)) {
			this.cacheManager.get().removeCache(this.cacheName);
		}
		this.cacheName = cacheName;
	}

	/**
	 * {@return the default time in seconds responses are cached}
	 */
	public long getDefaultCacheAge() {
		return this.defaultCacheAge;
	}

	/**
	 * Changes the default cache age.
	 * @param defaultCacheAge new default cache age in seconds
	 */
	public void setDefaultCacheAge(final long defaultCacheAge) {
		this.defaultCacheAge = defaultCacheAge;
	}

	/**
	 * Tries to find an endpoint for the given url. It first tries to find an endpoint
	 * within the configured endpoints by a matching url scheme. If that results in an
	 * empty endpoint and auto discovery is enabled, a http GET request is made to the
	 * given url, checking for alternate links with the type
	 * {@code application/(json|xml)+oembed}.
	 * @param url the URL that should be embedded
	 * @return an optional endpoint for this url
	 */
	final Optional<OembedEndpoint> findEndpointFor(final String url) {
		Optional<OembedEndpoint> rv = this.endpoints.keySet()
			.stream()
			.filter(endpoint -> endpoint.getUrlSchemes().stream().map(String::trim).anyMatch(url::matches))
			.findFirst();
		if (rv.isEmpty() && this.autodiscovery) {
			try {
				final HttpResponse httpResponse = this.httpClient.execute(new HttpGet(url));
				if (httpResponse.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
					LOGGER.warn("Autodiscovery for {} failed, server returned error {}: {}", url,
							httpResponse.getStatusLine().getStatusCode(),
							EntityUtils.toString(httpResponse.getEntity()));
				}
				else {
					final Document document = Jsoup.parse(EntityUtils.toString(httpResponse.getEntity(), "UTF-8"), url);
					rv = document.getElementsByAttributeValue("rel", "alternate").stream().map(alternate -> {
						OembedEndpoint autodiscoveredEndpoint = null;
						try {
							if (alternate.attr("type").equalsIgnoreCase("application/json+oembed")) {
								autodiscoveredEndpoint = new AutodiscoveredOembedEndpoint(
										new URI(alternate.absUrl("href")), Format.json);
							}
							else if (alternate.attr("type").equalsIgnoreCase("text/xml+oembed")) {
								autodiscoveredEndpoint = new AutodiscoveredOembedEndpoint(
										new URI(alternate.absUrl("href")), Format.xml);
							}
						}
						catch (URISyntaxException ex) {
							// Just ignore them
						}
						return autodiscoveredEndpoint;
					}).filter(Objects::nonNull).findFirst();
				}
			}
			catch (IOException ex) {
				LOGGER.warn("Autodiscovery for {} failed: {}", url, ex.getMessage());
			}
		}
		return rv;
	}

	/**
	 * Executes the given HttpRequest {@code request} and returns an input stream for the
	 * responses content if no error occurred and the server returned a status code OK.
	 * @param request the request to be executed
	 * @return an inputstream to read the response
	 */
	final InputStream executeRequest(final HttpGet request) {
		InputStream rv = null;
		try {
			final HttpResponse httpResponse = this.httpClient.execute(request);
			if (httpResponse.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
				LOGGER.warn("Skipping '{}', server returned error {}: {}", request.getURI().toString(),
						httpResponse.getStatusLine().getStatusCode(), EntityUtils.toString(httpResponse.getEntity()));
			}
			else {
				rv = httpResponse.getEntity().getContent();
			}
		}
		catch (IOException ex) {
			LOGGER.warn("Skipping '{}', could not get a response: {}", request.getURI().toString(), ex.getMessage());
		}
		return rv;
	}

	/**
	 * Tries to find an {@link OembedResponse} for the URL {@code url}. If a cache manager
	 * is present, it tries that first. If an {@code OembedResponse} can be discovered and
	 * a cache manager is present, that response will be cached.
	 * @param url the URL that might be represented by oembed.
	 * @return an oembed response
	 */
	public Optional<OembedResponse> getOembedResponseFor(final String url) {
		final String trimmedUrl = Optional.ofNullable(url).map(String::trim).orElse("");
		if (trimmedUrl.isEmpty()) {
			LOGGER.debug("Ignoring empty url...");
			return Optional.empty();
		}

		var rv = this.cacheManager.map(cm -> cm.addCacheIfAbsent(this.cacheName).get(trimmedUrl))
			.map(element -> (OembedResponse) element.getObjectValue());
		// If there's already an oembed response cached, use that
		if (rv.isPresent()) {
			LOGGER.debug("Using OembedResponse from cache for '{}'...", trimmedUrl);
			return rv;
		}

		final Optional<OembedEndpoint> endPoint = this.findEndpointFor(trimmedUrl);
		LOGGER.debug("Found endpoint {} for '{}'...", endPoint, trimmedUrl);
		rv = endPoint
			.map(ep -> this.endpoints.getOrDefault(ep, this.defaultRequestProvider)
				.createRequestFor(this.userAgent, this.applicationName, ep.toApiUrl(trimmedUrl)))
			.map(this::executeRequest)
			.map(content -> {
				OembedResponse oembedResponse = null;
				try {
					oembedResponse = this.parsers.get(endPoint.get().getFormat()).unmarshal(content);
				}
				catch (OembedException ex) {
					LOGGER.warn("Server returned an invalid oembed format for url '{}': {}", trimmedUrl,
							ex.getMessage());
				}
				return oembedResponse;
			});

		if (this.cacheManager.isPresent()) {
			final Ehcache cache = this.cacheManager.get().addCacheIfAbsent(this.cacheName);
			// Cache at least 60 seconds
			final int cacheAge = (int) Math.min(
					Math.max(60L, rv.map(OembedResponse::getCacheAge).orElse(this.defaultCacheAge)), Integer.MAX_VALUE);
			// We're adding failed urls to the cache as well to prevent them
			// from being tried again over and over (at least for some seconds)
			cache.put(new net.sf.ehcache.Element(trimmedUrl, rv.orElse(null), cacheAge, cacheAge));
			LOGGER.debug("Cached {} for {} seconds for url '{}'...", rv, cacheAge, trimmedUrl);
		}

		return rv;
	}

	/**
	 * Embed all urls found in the given text for which providers are present.
	 * @param textWithEmbeddableUrls text that may contain links
	 * @param baseUrl base url for constructing absolute links
	 * @return a string with urls embedded
	 * @see #embedUrls(java.lang.String, java.lang.String, java.lang.Class)
	 */
	public String embedUrls(final String textWithEmbeddableUrls, final String baseUrl) {
		return embedUrls(textWithEmbeddableUrls, baseUrl, String.class);
	}

	/**
	 * Scans the text {@code textWithEmbeddableUrls} for anchor tags and tries to find
	 * {@link OembedEndpoint} for those urls. If such an endpoint exists, it tries to get
	 * an {@link OembedResponse} of that url from the endpoint. This response will then be
	 * rendered as html and is used to replace the anchor tag.
	 * @param <T> type of the resulting document with embedded links
	 * @param textWithEmbeddableUrls text that contains embeddable urls
	 * @param baseUrl an optional base url for resolving relative urls
	 * @param targetClass the concrete class for the document node
	 * @return the same text with embedded urls if such urls existed
	 */
	@SuppressWarnings("unchecked")
	public <T> T embedUrls(final String textWithEmbeddableUrls, final String baseUrl,
			final Class<? extends T> targetClass) {
		var optionalBaseUrl = Optional.ofNullable(baseUrl);
		T rv;
		if (String.class.isAssignableFrom(targetClass)) {
			rv = (T) textWithEmbeddableUrls;
		}
		else if (Document.class.isAssignableFrom(targetClass)) {
			rv = (T) Document.createShell(optionalBaseUrl.orElse(""));
		}
		else {
			throw new OembedException(String.format("Invalid target class: %s", targetClass.getName()));
		}

		if (!(textWithEmbeddableUrls == null || textWithEmbeddableUrls.trim().isEmpty())) {
			// Create a document
			final Document document = embedUrls(
					Jsoup.parseBodyFragment(textWithEmbeddableUrls, optionalBaseUrl.orElse("")));
			if (Document.class.isAssignableFrom(targetClass)) {
				rv = (T) document;
			}
			else {
				document.outputSettings()
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
	 * @param document an existing document, will be modified
	 * @return the modified document with embedded urls
	 * @see #embedUrls(java.lang.String, java.lang.String, java.lang.Class)
	 */
	public Document embedUrls(final Document document) {
		for (Element a : document.getElementsByTag("a")) {
			final String absUrl = a.absUrl("href");
			final Optional<String> html = this.getOembedResponseFor(absUrl).map(response -> {
				final OembedResponseRenderer renderer = this.renderers.entrySet()
					.stream()
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
	 * @param format the format for which a parser is needed
	 * @return the parser if a parser for the given format exists
	 */
	public OembedParser getParser(final Format format) {
		return this.parsers.get(format);
	}

}
