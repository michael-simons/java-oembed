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

import ac.simons.oembed.OembedResponse.Format;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.message.BasicNameValuePair;

/**
 * This describes an oembed endpoint, with its name, url schemes etc. Such an
 * endpoint can be embedded in several text documents around <em>Daily
 * Fratze</em>.
 * <br>
 * This class is implemented as a plain java bean so that it can be used without
 * much hassle inside spring {@code @Configuration}.
 *
 * @author Michael J. Simons, 2014-12-30
 */
public class OembedEndpoint {

	/**
	 * The name of this provider.
	 */
	private String name;

	/**
	 * The actual endpoint, that is the base URL for computations inside
	 * {@link #toApiUrl}.
	 */
	private String endpoint;

	/**
	 * The format that this provider supports.
	 */
	private Format format = Format.json;

	/**
	 * If set to a nun-null value the maximum width that should be requested
	 * from the endpoint.
	 */
	private Integer maxWidth;

	/**
	 * If set to a nun-null value the maximum height that should be requested
	 * from the endpoint.
	 */
	private Integer maxHeight;

	/**
	 * The list of supported url schemes.
	 */
	private List<String> urlSchemes;

	/**
	 * The request provider that should be instantiated for this endpoint. Must
	 * have default constructor.
	 */
	private Class<? extends RequestProvider> requestProviderClass = DefaultRequestProvider.class;

	/**
	 * The list of properties for the configureded
	 * {@link #requestProviderClass}.
	 */
	private Map<String, String> requestProviderProperties;

	/**
	 * The response renderer class for this endoint.
	 */
	private Class<? extends OembedResponseRenderer> responseRendererClass = DefaultOembedResponseRenderer.class;

	/**
	 * The list of properties for the configureded
	 * {@link #responseRendererClass}.
	 */
	private Map<String, String> responseRendererProperties;

	/**
	 * @return The name of this provider
	 */
	public String getName() {
		return name;
	}

	/**
	 * Update the name of this provider.
	 *
	 * @param name The new name
	 */
	public void setName(final String name) {
		this.name = name;
	}

	/**
	 * @return The endpoint of this provider
	 */
	public String getEndpoint() {
		return endpoint;
	}

	/**
	 * Updates the endpoint of this provider. Any {@code .{format}} parameter
	 * will be recognized.
	 *
	 * @param endpoint The new endpoint
	 */
	public void setEndpoint(final String endpoint) {
		this.endpoint = endpoint;
	}

	/**
	 * @return The format that this provider supports
	 */
	public Format getFormat() {
		return format;
	}

	/**
	 * Updates the format of this endpoint.
	 *
	 * @param format The new format
	 */
	public void setFormat(final Format format) {
		this.format = format;
	}

	/**
	 * @return The maximum width requested by this endpoint
	 */
	public Integer getMaxWidth() {
		return maxWidth;
	}

	/**
	 * Updates the maximum width requested by this endpoint.
	 *
	 * @param maxWidth The new maximum width. Can be null.
	 */
	public void setMaxWidth(final Integer maxWidth) {
		this.maxWidth = maxWidth;
	}

	/**
	 * @return The maximum height requested by this endpoint
	 */
	public Integer getMaxHeight() {
		return maxHeight;
	}

	/**
	 * Updates the maximum height requested by this endpoint.
	 *
	 * @param maxHeight The new maximum height. Can be null.
	 */
	public void setMaxHeight(final Integer maxHeight) {
		this.maxHeight = maxHeight;
	}

	/**
	 * @return The list of recognized url schemes
	 */
	public List<String> getUrlSchemes() {
		return urlSchemes;
	}

	/**
	 * Updates the list of recognized url schemes.
	 *
	 * @param urlSchemes A new list of url schemes. May not be null-
	 */
	public void setUrlSchemes(final List<String> urlSchemes) {
		this.urlSchemes = urlSchemes;
	}

	/**
	 * @return The class of the request provider for this endpoint
	 */
	public Class<? extends RequestProvider> getRequestProviderClass() {
		return requestProviderClass;
	}

	/**
	 * Update the request provider class.
	 *
	 * @param requestProviderClass New request provider class
	 */
	public void setRequestProviderClass(final Class<? extends RequestProvider> requestProviderClass) {
		this.requestProviderClass = requestProviderClass;
	}

	/**
	 * @return Additional properties for the request provider instance
	 */
	public Map<String, String> getRequestProviderProperties() {
		return requestProviderProperties;
	}

	/**
	 * Update the properties of the request provider instance.
	 *
	 * @param requestProviderProperties New map of properties
	 */
	public void setRequestProviderProperties(final Map<String, String> requestProviderProperties) {
		this.requestProviderProperties = requestProviderProperties;
	}

	/**
	 * @return The class of the response renderer for this endpoint
	 */
	public Class<? extends OembedResponseRenderer> getResponseRendererClass() {
		return responseRendererClass;
	}

	/**
	 * Update the response renderer class.
	 *
	 * @param responseRendererClass New response renderer class
	 */
	public void setResponseRendererClass(final Class<? extends OembedResponseRenderer> responseRendererClass) {
		this.responseRendererClass = responseRendererClass;
	}

	/**
	 * @return Additional properties for the response renderer instance
	 */
	public Map<String, String> getResponseRendererProperties() {
		return responseRendererProperties;
	}

	/**
	 * Update the properties of the response renderer instance.
	 *
	 * @param responseRendererProperties New map of properties
	 */
	public void setResponseRendererProperties(final Map<String, String> responseRendererProperties) {
		this.responseRendererProperties = responseRendererProperties;
	}

	/**
	 * Creates an URI that can be called to retrieve an oembed response for the
	 * url {@code url}.
	 *
	 * @param url The url for which an oembed api url should be created
	 * @return An api url that hopefully returns an oembed response for
	 * {@code url}
	 * @throws OembedException Any exceptions that occur during building the url
	 */
	public URI toApiUrl(final String url) {
		String uri;
		final List<NameValuePair> query = new ArrayList<>();

		if (this.getEndpoint().toLowerCase().contains("%{format}")) {
			uri = this.getEndpoint().replaceAll(Pattern.quote("%{format}"), this.getFormat().toString());
		} else {
			uri = this.getEndpoint();
			query.add(new BasicNameValuePair("format", this.getFormat().toString()));
		}
		query.add(new BasicNameValuePair("url", url));
		if (this.getMaxWidth() != null) {
			query.add(new BasicNameValuePair("maxwidth", this.getMaxWidth().toString()));
		}
		if (this.getMaxHeight() != null) {
			query.add(new BasicNameValuePair("maxheight", this.getMaxHeight().toString()));
		}

		try {
			return new URIBuilder(uri).addParameters(query).build();
		} catch (URISyntaxException ex) {
			throw new OembedException(ex);
		}
	}
}
