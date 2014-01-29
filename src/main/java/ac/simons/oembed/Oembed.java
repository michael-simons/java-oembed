/**
 * Created by Michael Simons, michael-simons.eu
 * and released under The BSD License
 * http://www.opensource.org/licenses/bsd-license.php
 *
 * Copyright (c) 2010, Michael Simons
 * All rights reserved.
 *
 * Redistribution  and  use  in  source   and  binary  forms,  with  or   without
 * modification, are permitted provided that the following conditions are met:
 *
 * * Redistributions of source   code must retain   the above copyright   notice,
 *   this list of conditions and the following disclaimer.
 *
 * * Redistributions in binary  form must reproduce  the above copyright  notice,
 *   this list of conditions  and the following  disclaimer in the  documentation
 *   and/or other materials provided with the distribution.
 *
 * * Neither the name  of  michael-simons.eu   nor the names  of its contributors
 *   may be used  to endorse   or promote  products derived  from  this  software
 *   without specific prior written permission.
 *
 * THIS SOFTWARE IS  PROVIDED BY THE  COPYRIGHT HOLDERS AND  CONTRIBUTORS "AS IS"
 * AND ANY  EXPRESS OR  IMPLIED WARRANTIES,  INCLUDING, BUT  NOT LIMITED  TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL  THE COPYRIGHT HOLDER OR CONTRIBUTORS  BE LIABLE
 * FOR ANY  DIRECT, INDIRECT,  INCIDENTAL, SPECIAL,  EXEMPLARY, OR  CONSEQUENTIAL
 * DAMAGES (INCLUDING,  BUT NOT  LIMITED TO,  PROCUREMENT OF  SUBSTITUTE GOODS OR
 * SERVICES; LOSS  OF USE,  DATA, OR  PROFITS; OR  BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT  LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE  USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package ac.simons.oembed;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Ehcache;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Entities.EscapeMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Michael J. Simons
 */
public class Oembed {
	private final Long MIN_VALID_CACHE_AGE = new Long(Integer.MIN_VALUE);
	private final Long MAX_VALID_CACHE_AGE = new Long(Integer.MAX_VALUE);
	
	/** The logger */
	private final Logger logger = LoggerFactory.getLogger(Oembed.class);
	/** The HttpClient instance for all  requests. It should be configured for multithreading */
	private final HttpClient httpClient;
	/** The map of known providers */
	private Map<String, OembedProvider> provider = new HashMap<>();
	/** Optional handlers for providers registered in {@link #provider} */
	private Map<String, OembedResponseHandler> handler = new HashMap<>();
	/** The map of all known parsers. For now, only json and xml exists */
	private Map<String, OembedParser> parser;
	/** Optional ehcache client for caching valid oembed responses */
	private CacheManager cacheManager;
	/** Time in seconds responses are cached. Used if the response has no cache_age */
	private int defaultCacheAge = 3600;
	/** Flag, if autodiscovery is enabled when there is no provider for a specific url. Defaults to false */
	private boolean autodiscovery = false;
	/** If this is not null and <tt>cacheManager</tt> is not null, failed urls aren't called for the given amount of seconds */
	private Long ignoreFailedUrlsForSeconds = new Long(24 * 60 * 60);
	private String baseUri = "";
	/** Name of the ehcache, defaults to the fully qualified name of Oembed */
	private String cacheName = Oembed.class.getName();
	/** The default user agent */
	private String userAgent;
	/** An optional string that is appended to the user agent */
	private String consumer;

	/**
	 * Constructs the Oembed Api with the default parsers (json and xml) and 
	 * an empty map of provider
	 * @param httpClient
	 */
	public Oembed(final HttpClient httpClient) {
		this.httpClient = httpClient;

		this.parser = new HashMap<>();
		this.parser.put("json", new OembedJsonParser());
		this.parser.put("xml", new OembedXmlParser());
		
		final Properties version = new Properties();
		try {
			version.load(Oembed.class.getResourceAsStream("/ac/simons/oembed/version.properties"));
		} catch(IOException e) {
		}
		this.userAgent = String.format("Java/%s java-oembed/%s", System.getProperty("java.version"), version.getProperty("ac.simons.oembed.version"));
		logger.info(String.format("Oembed (%s) ready...", this.userAgent));
	}

	public Map<String, OembedProvider> getProvider() {
		return provider;
	}

	public void setProvider(Map<String, OembedProvider> provider) {
		this.provider = provider;
	}

	public OembedParser getParser(final String format) {
		return this.parser.get(format);
	}

	/**
	 * Sets the parser for the given format, returns the old parser if any
	 * @param format
	 * @param parser
	 * @return
	 */
	public OembedParser setParser(final String format, final OembedParser parser) {
		return this.parser.put(format, parser);
	}
	
	/**
	 * Transforms the given URL into an OembedResponse. Returns null if
	 * there is no provider configured for this url.
	 * @param url
	 * @return
	 * @throws OembedException
	 */
	public OembedResponse transformUrl(final String url) throws OembedException {
		OembedResponse response = null;
		if(url == null || url.length() == 0) {
			logger.warn("Can't embed an empty url!");
		} else {
			if(cacheManager != null) {
				try {
					logger.debug("Trying to use ehcache");					
					response = getFromCache(url);					
				} catch (Exception e) {
					logger.warn(String.format("There was a problem with ehcache: %s", e.getMessage()), e);
				}
			}

			if(response != null) {
				logger.debug("Using cached result...");
				if(response.isEmpty())
					response = null;
			} else {
				OembedProvider provider = this.findProvider(url);
				if(provider == null && (!this.isAutodiscovery() || (provider = autodiscoverOembedURIForUrl(url)) == null))
					logger.info(String.format("No oembed provider for url %s and autodiscovery is disabled or found no result", url));
				else {
					try {
						final URI api = provider.toApiUrl(url);
						logger.debug(String.format("Calling url %s", api.toString()));
						final HttpGet request = new HttpGet(api);
						if(this.userAgent != null)
							request.setHeader("User-Agent", String.format("%s%s", this.userAgent, this.consumer == null ? "" : "; " + this.consumer));						
						final HttpResponse httpResponse = this.httpClient.execute(provider.getHttpRequestDecorator().decorate(request));
						if(httpResponse.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
							logger.warn(String.format("Server returned error %d for '%s': %s", httpResponse.getStatusLine().getStatusCode(), url, EntityUtils.toString(httpResponse.getEntity())));
							if(ignoreFailedUrlsForSeconds != null && cacheManager != null) {
								final OembedResponse emptyResponse = new OembedResponse();
								emptyResponse.setEmpty(true);
								emptyResponse.setCacheAge(ignoreFailedUrlsForSeconds);
								this.addToCache(url, emptyResponse);
							}								
						} else {
							response = this.getParser(provider.getFormat().toLowerCase()).unmarshal(httpResponse.getEntity().getContent());
							response.setSource(provider.getName());
							response.setOriginalUrl(url);
							if(this.cacheManager != null) {
								try {
									this.addToCache(url, response);
								} catch(Exception e) {
									logger.warn(String.format("Could not cache response for %s: %s", url, e.getMessage(), e));
								}
							}
						}
					} catch(IOException | URISyntaxException e) {
						throw new OembedException(e);
					} catch(NullPointerException e) {
						throw new OembedException(String.format("NPE, probably invalid format :%s", provider.getFormat()));
					} catch(Exception e) {
						throw new OembedException(e);
					}
				}
			}
		}

		return response;
	}
	
	public String transformDocumentString(final String documentHtml) {
		final Document rv = transformDocument(documentHtml);
		rv.outputSettings().prettyPrint(false).escapeMode(EscapeMode.xhtml);
		return rv.body().html();
	}
	
	public Document transformDocument(final String documentHtml) {
		return transformDocument(Jsoup.parseBodyFragment(documentHtml, baseUri));		
	}
	
	/**
	 * Parses  the given html document into a document and processes 
	 * all anchor elements. If a valid anchor is found, it tries to
	 * get an oembed response for it's url and than render the result
	 * into the document replacing the given anchor.<br>
	 * It returns the html representation of the new document.<br>
	 * If there's an error or no oembed result for an url, the anchor tag
	 * will be left as it was. 
	 * @param documentHtml
	 * @return
	 */
	public Document transformDocument(final Document document) {
		boolean changedBaseUri = false;
		if(document.baseUri() == null && this.getBaseUri() != null) {
			document.setBaseUri(this.getBaseUri());
			changedBaseUri = true;
		}
		for(Element a : document.getElementsByTag("a")) {
			final String href = a.absUrl("href");			
			try {
				String renderedRespose = null;
				final OembedResponse oembedResponse = this.transformUrl(href);
				// There was no response or an exception happened
				if(oembedResponse == null)
					continue;				
				// There is a handler for this response
				else if(this.getHandler().containsKey(oembedResponse.getSource()))
					this.getHandler().get(oembedResponse.getSource()).handle(document, a, oembedResponse);
				// Try to render the response itself and replace the current anchor
				else if((renderedRespose = oembedResponse.render()) != null) {
					a.before(renderedRespose);
					a.remove();
				}
			} catch (OembedException e) {
				logger.warn(String.format("Skipping '%s': %s", href, e.getMessage()));
			}
		}
		if(changedBaseUri)
			document.setBaseUri(null);
		return document;
	}

	/**
	 * Finds a provider for the given url
	 * @param url
	 * @return
	 */
	private OembedProvider findProvider(final String url) {
		OembedProvider rv = null;
		providerLoop:
			for(OembedProvider provider : this.provider.values()) {
				for(String urlScheme : provider.getUrlSchemes()) {
					if(url.matches(urlScheme)) {
						rv = provider;
						break providerLoop;
					}	
				}
			}		
		return rv;
	}

	private OembedProvider autodiscoverOembedURIForUrl(final String url) {
		OembedProvider rv = null;
		
		try {
			final HttpGet request = new HttpGet(url);
			final HttpResponse httpResponse = this.httpClient.execute(request);
			if(httpResponse.getStatusLine().getStatusCode() != HttpStatus.SC_OK)
				logger.warn(String.format("Autodiscovery for %s failed, server returned error %d: %s", url, httpResponse.getStatusLine().getStatusCode(), EntityUtils.toString(httpResponse.getEntity())));
			else {				
				final URI uri = request.getURI();
				final Document document = Jsoup.parse(EntityUtils.toString(httpResponse.getEntity(), "UTF-8"), String.format("%s://%s:%d", uri.getScheme(), uri.getHost(), uri.getPort()));				
				for(Element alternate : document.getElementsByAttributeValue("rel", "alternate")) {					
					if(alternate.attr("type").equalsIgnoreCase("application/json+oembed"))
						rv = new AutodiscoveredOembedProvider(url, new URI(alternate.absUrl("href")), "json");
					else if(alternate.attr("type").equalsIgnoreCase("text/xml+oembed"))
						rv = new AutodiscoveredOembedProvider(url, new URI(alternate.absUrl("href")), "xml");
					if(rv != null)
						break;
				}
			}
		} catch(Exception e) {
			logger.warn(String.format("Autodiscovery for %s failedd: %s", url, e.getMessage()), e);
		}
		 
		return rv;
	}

	public CacheManager getCacheManager() {
		return cacheManager;
	}

	public void setCacheManager(CacheManager cacheManager) {
		this.cacheManager = cacheManager;
	}

	public int getDefaultCacheAge() {
		return defaultCacheAge;
	}

	public void setDefaultCacheAge(int defaultCacheAge) {
		this.defaultCacheAge = defaultCacheAge;
	}

	public Map<String, OembedResponseHandler> getHandler() {
		return handler;
	}

	public void setHandler(Map<String, OembedResponseHandler> handler) {
		this.handler = handler;
	}

	public Map<String, OembedParser> getParser() {
		return parser;
	}

	public void setParser(Map<String, OembedParser> parser) {
		this.parser = parser;
	}

	public boolean isAutodiscovery() {
		return autodiscovery;
	}

	public void setAutodiscovery(boolean autodiscovery) {
		this.autodiscovery = autodiscovery;
	}

	public String getBaseUri() {
		return baseUri;
	}

	public void setBaseUri(String baseUri) {
		this.baseUri = baseUri;
	}

	public String getCacheName() {
		return cacheName;
	}

	public void setCacheName(String cacheName) {
		this.cacheName = cacheName;
	}		
	
	protected OembedResponse getFromCache(final String key) {
		Ehcache cache = this.cacheManager.getCache(this.cacheName);
		if(cache == null) {
			this.cacheManager.addCache(this.cacheName);
			cache = this.cacheManager.getCache(this.cacheName);
		}
		OembedResponse rv = null;
		final net.sf.ehcache.Element element = cache.get(key);
		if(element != null)
			rv = (OembedResponse) element.getObjectValue();
		return rv;
	}	
	
	protected void addToCache(final String url, final OembedResponse response) {
		final Ehcache cache = this.cacheManager.getCache(this.cacheName);		
		cache.put(new net.sf.ehcache.Element(url, response, null, null, response.getCacheAge() != null ? new Long(Math.min(Math.max(MIN_VALID_CACHE_AGE, response.getCacheAge()), MAX_VALID_CACHE_AGE)).intValue() : this.getDefaultCacheAge()));
	}

	public Long getIgnoreFailedUrlsForSeconds() {
		return ignoreFailedUrlsForSeconds;
	}

	public void setIgnoreFailedUrlsForSeconds(Long ignoreFailedUrlsForSeconds) {
		this.ignoreFailedUrlsForSeconds = ignoreFailedUrlsForSeconds;
	}

	public String getUserAgent() {
		return userAgent;
	}

	public void setUserAgent(String userAgent) {
		this.userAgent = userAgent;
	}

	public String getConsumer() {
		return consumer;
	}

	public void setConsumer(String consumer) {
		this.consumer = consumer;
	}
}