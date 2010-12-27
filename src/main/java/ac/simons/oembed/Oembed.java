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

import net.rubyeye.xmemcached.MemcachedClient;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Michael J. Simons
 */
public class Oembed {
	/** The logger */
	private final Logger logger = LoggerFactory.getLogger(Oembed.class);
	/** The HttpClient instance for all  requests. It should be configured for multithreading */
	private final HttpClient httpClient;
	/** The map of known providers */
	private Map<String, OembedProvider> provider = new HashMap<String, OembedProvider>();
	/** Optional handlers for providers registered in {@link #provider} */
	private Map<String, OembedResponseHandler> handler = new HashMap<String, OembedResponseHandler>();
	/** The map of all known parsers. For now, only json and xml exists */
	private Map<String, OembedParser> parser;
	/** Optional memcached client for caching valid oembed responses */
	private MemcachedClient memcachedClient;
	/** Time in seconds responses are cached. Used if the response has no cache_age */
	private int defaultCacheAge = 3600;
	/** Flag, if autodiscovery is enabled when there is no provider for a specific url. Defaults to false */
	private boolean autodiscovery = false;

	/**
	 * Constructs the Oembed Api with the default parsers (json and xml) and 
	 * an empty map of provider
	 * @param httpClient
	 */
	public Oembed(final HttpClient httpClient) {
		this.httpClient = httpClient;

		this.parser = new HashMap<String, OembedParser>();
		this.parser.put("json", new OembedJsonParser());
		this.parser.put("xml", new OembedXmlParser());
	}

	/**
	 * Adds the given provider to the map if the format is supported
	 * @param provider
	 * @return
	 * @throws OembedException
	 */
	public Oembed withProvider(final OembedProvider provider) throws OembedException {
		if(!this.parser.containsKey(provider.getFormat().toLowerCase()))
			throw new OembedException(String.format("Invalid format %s", provider.getFormat()));

		if(this.getProvider() == null)
			this.setProvider(new HashMap<String, OembedProvider>());
		this.getProvider().put(provider.getName(), provider);
		return this;
	}
	
	public Oembed withHandler(final OembedResponseHandler handler) {
		if(this.getHandler() == null)
			this.setHandler(new HashMap<String, OembedResponseHandler>());
		this.getHandler().put(handler.getFor(), handler);
		return this;
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
			if(memcachedClient != null) {
				try {
					logger.debug("Trying to use memcached");					
					response = memcachedClient.get(url);
				} catch (Exception e) {
					logger.warn(String.format("There was a problem with memcached: %s", e.getMessage()), e);
				}
			}

			if(response != null)
				logger.debug("Using cached result...");
			else {
				OembedProvider provider = this.findProvider(url);
				if(provider == null && (!this.isAutodiscovery() || (provider = autodiscoverOembedURIForUrl(url)) == null))
					logger.info(String.format("No oembed provider for url %s and autodiscovery is disabled or found no result", url));
				else {
					try {
						final URI api = provider.toApiUrl(url);
						logger.debug(String.format("Calling url %s", api.toString()));
						final HttpResponse httpResponse = this.httpClient.execute(new HttpGet(api));
						if(httpResponse.getStatusLine().getStatusCode() != HttpStatus.SC_OK)
							logger.warn(String.format("Server returned error %d: %s", httpResponse.getStatusLine().getStatusCode(), EntityUtils.toString(httpResponse.getEntity())));
						else {
							response = this.getParser(provider.getFormat().toLowerCase()).unmarshal(httpResponse.getEntity().getContent());
							response.setSource(provider.getName());
							response.setOriginalUrl(url);
							if(this.memcachedClient != null) {
								try {
									this.memcachedClient.add(url, response.getCacheAge() != null ? response.getCacheAge() : this.getDefaultCacheAge(), response);
								} catch(Exception e) {
									logger.warn(String.format("Could not cache response for %s: %s", url, e.getMessage(), e));
								}
							}
						}
					} catch(IOException e) {
						throw new OembedException(e);
					} catch(NullPointerException e) {
						throw new OembedException(String.format("NPE, probably invalid format :%s", provider.getFormat()));
					} catch (URISyntaxException e) {
						throw new OembedException(e);
					}
				}
			}
		}

		return response;
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
	public String transformDocument(final String documentHtml) {
		final Document document = Jsoup.parseBodyFragment(documentHtml, "");
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
			}
		}
		return document.body().html();
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
			final HttpResponse httpResponse = this.httpClient.execute(new HttpGet(url));
			if(httpResponse.getStatusLine().getStatusCode() != HttpStatus.SC_OK)
				logger.warn(String.format("Autodiscovery for %s failed, server returned error %d: %s", url, httpResponse.getStatusLine().getStatusCode(), EntityUtils.toString(httpResponse.getEntity())));
			else {				
				final Document document = Jsoup.parse(EntityUtils.toString(httpResponse.getEntity(), "UTF-8"));
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

	public MemcachedClient getMemcachedClient() {
		return memcachedClient;
	}

	public void setMemcachedClient(MemcachedClient memcachedClient) {
		this.memcachedClient = memcachedClient;
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

	public boolean isAutodiscovery() {
		return autodiscovery;
	}

	public void setAutodiscovery(boolean autodiscovery) {
		this.autodiscovery = autodiscovery;
	}	
}