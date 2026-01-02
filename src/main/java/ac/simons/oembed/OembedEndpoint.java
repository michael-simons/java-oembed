/*
 * Created by Michael Simons, michael-simons.eu
 * and released under The BSD License
 * http://www.opensource.org/licenses/bsd-license.php
 *
 * Copyright (c) 2010-2026, Michael Simons
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

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import ac.simons.oembed.OembedResponse.Format;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.message.BasicNameValuePair;

/**
 * This describes an oembed endpoint, with its name, url schemes etc. Such an endpoint can
 * be embedded in several text documents around <em>Daily Fratze</em>. <br>
 * This class is implemented as a plain java bean so that it can be used without much
 * hassle inside spring {@code @Configuration}.
 *
 * @author Michael J. Simons
 * @since 2014-12-30
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
	 * If set to a nun-null value the maximum width that should be requested from the
	 * endpoint.
	 */
	private Integer maxWidth;

	/**
	 * If set to a nun-null value the maximum height that should be requested from the
	 * endpoint.
	 */
	private Integer maxHeight;

	/**
	 * The list of supported url schemes.
	 */
	private List<String> urlSchemes;

	/**
	 * The request provider that should be instantiated for this endpoint. Must have
	 * default constructor.
	 */
	private Class<? extends RequestProvider> requestProviderClass = DefaultRequestProvider.class;

	/**
	 * The list of properties for the configureded {@link #requestProviderClass}.
	 */
	private Map<String, String> requestProviderProperties;

	/**
	 * The response renderer class for this endoint.
	 */
	private Class<? extends OembedResponseRenderer> responseRendererClass = DefaultOembedResponseRenderer.class;

	/**
	 * The list of properties for the configureded {@link #responseRendererClass}.
	 */
	private Map<String, String> responseRendererProperties;

	/**
	 * {@return the name of this provider}
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Updates the name of this provider.
	 * @param name the new name
	 */
	public void setName(final String name) {
		this.name = name;
	}

	/**
	 * {@return the endpoint of this provider}
	 */
	public String getEndpoint() {
		return this.endpoint;
	}

	/**
	 * Updates the endpoint of this provider. Any {@code .{format}} parameter will be
	 * recognized.
	 * @param endpoint the new endpoint
	 */
	public void setEndpoint(final String endpoint) {
		this.endpoint = endpoint;
	}

	/**
	 * {@return the format that this provider supports}
	 */
	public Format getFormat() {
		return this.format;
	}

	/**
	 * Updates the format of this endpoint.
	 * @param format the new format
	 */
	public void setFormat(final Format format) {
		this.format = format;
	}

	/**
	 * {@return the maximum width requested by this endpoint}
	 */
	public Integer getMaxWidth() {
		return this.maxWidth;
	}

	/**
	 * Updates the maximum width requested by this endpoint.
	 * @param maxWidth the new maximum width. Can be null.
	 */
	public void setMaxWidth(final Integer maxWidth) {
		this.maxWidth = maxWidth;
	}

	/**
	 * {@return the maximum height requested by this endpoint}
	 */
	public Integer getMaxHeight() {
		return this.maxHeight;
	}

	/**
	 * Updates the maximum height requested by this endpoint.
	 * @param maxHeight the new maximum height. Can be null.
	 */
	public void setMaxHeight(final Integer maxHeight) {
		this.maxHeight = maxHeight;
	}

	/**
	 * {@return the list of recognized url schemes}
	 */
	public List<String> getUrlSchemes() {
		return this.urlSchemes;
	}

	/**
	 * Updates the list of recognized url schemes.
	 * @param urlSchemes a new list of url schemes, may not be null-
	 */
	public void setUrlSchemes(final List<String> urlSchemes) {
		this.urlSchemes = urlSchemes;
	}

	/**
	 * {@return the class of the request provider for this endpoint}
	 */
	public Class<? extends RequestProvider> getRequestProviderClass() {
		return this.requestProviderClass;
	}

	/**
	 * Update the request provider class.
	 * @param requestProviderClass new request provider class
	 */
	public void setRequestProviderClass(final Class<? extends RequestProvider> requestProviderClass) {
		this.requestProviderClass = requestProviderClass;
	}

	/**
	 * {@return additional properties for the request provider instance}
	 */
	public Map<String, String> getRequestProviderProperties() {
		return this.requestProviderProperties;
	}

	/**
	 * Update the properties of the request provider instance.
	 * @param requestProviderProperties new map of properties
	 */
	public void setRequestProviderProperties(final Map<String, String> requestProviderProperties) {
		this.requestProviderProperties = requestProviderProperties;
	}

	/**
	 * {@return the class of the response renderer for this endpoint}
	 */
	public Class<? extends OembedResponseRenderer> getResponseRendererClass() {
		return this.responseRendererClass;
	}

	/**
	 * Update the response renderer class.
	 * @param responseRendererClass new response renderer class
	 */
	public void setResponseRendererClass(final Class<? extends OembedResponseRenderer> responseRendererClass) {
		this.responseRendererClass = responseRendererClass;
	}

	/**
	 * {@return additional properties for the response renderer instance}
	 */
	public Map<String, String> getResponseRendererProperties() {
		return this.responseRendererProperties;
	}

	/**
	 * Update the properties of the response renderer instance.
	 * @param responseRendererProperties new map of properties
	 */
	public void setResponseRendererProperties(final Map<String, String> responseRendererProperties) {
		this.responseRendererProperties = responseRendererProperties;
	}

	/**
	 * Creates a URI that can be called to retrieve an oembed response for the url
	 * {@code url}.
	 * @param url the url for which an oembed api url should be created
	 * @return an api url that hopefully returns an oembed response for {@code url}
	 * @throws OembedException any exceptions that occur during building the url
	 */
	public URI toApiUrl(final String url) {
		String uri;
		final List<NameValuePair> query = new ArrayList<>();

		if (this.getEndpoint().toLowerCase().contains("%{format}")) {
			uri = this.getEndpoint().replaceAll(Pattern.quote("%{format}"), this.getFormat().toString());
		}
		else {
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
		}
		catch (URISyntaxException ex) {
			throw new OembedException(ex);
		}
	}

}
