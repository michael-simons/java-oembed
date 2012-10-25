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

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;

/**
 * @author Michael J. Simons
 */
public class DefaultOembedProvider implements OembedProvider {
	private String name;
	private String endpoint;
	private String format;
	private Integer maxWidth;
	private Integer maxHeight;
	private List<String> urlSchemes;
	/** A decorator that can manipulate / decorate the request before executing */
	private HttpRequestDecorator httpRequestDecorator = new DefaultHttpRequestDecorator();
		
	public URI toApiUrl(final String url) throws URISyntaxException {
		String uri = null;
		final List<NameValuePair> query = new ArrayList<NameValuePair>();

		if(this.getEndpoint().toLowerCase().contains("%{format}"))
			uri = this.getEndpoint().replaceAll(Pattern.quote("%{format}"), this.getFormat());
		else {
			uri = this.getEndpoint();
			query.add(new BasicNameValuePair("format", this.getFormat()));
		}
		query.add(new BasicNameValuePair("url", url));
		if(this.getMaxWidth() != null)
			query.add(new BasicNameValuePair("maxwidth", this.getMaxWidth().toString()));
		if(this.getMaxHeight() != null)
			query.add(new BasicNameValuePair("maxheight", this.getMaxHeight().toString()));
		return new URI(String.format("%s?%s", uri, URLEncodedUtils.format(query, "UTF-8")));
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getEndpoint() {
		return endpoint;
	}
	
	public void setEndpoint(String apiEndpoint) {
		this.endpoint = apiEndpoint;
	}
	
	public List<String> getUrlSchemes() {
		return urlSchemes;
	}
	
	public void setUrlSchemes(List<String> urlSchemes) {
		this.urlSchemes = urlSchemes;
	}

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}

	public Integer getMaxWidth() {
		return maxWidth;
	}

	public void setMaxWidth(Integer maxWidth) {
		this.maxWidth = maxWidth;
	}

	public Integer getMaxHeight() {
		return maxHeight;
	}

	public void setMaxHeight(Integer maxHeight) {
		this.maxHeight = maxHeight;
	}

	public HttpRequestDecorator getHttpRequestDecorator() {
		return httpRequestDecorator;
	}

	public void setHttpRequestDecorator(HttpRequestDecorator httpRequestDecorator) {
		this.httpRequestDecorator = httpRequestDecorator;
	}
}