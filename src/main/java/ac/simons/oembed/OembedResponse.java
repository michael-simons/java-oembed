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

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * This represents a valid OEmbed response according to the specs from
 * <a href="http://oembed.com">oembed.com</a>. In case anything breaks check the endpoint
 * in questions returns a valid response (types etc.).
 *
 * @author Michael J. Simons
 * @since 2010-12-24
 */
@XmlRootElement(name = "oembed")
@XmlAccessorType(XmlAccessType.FIELD)
@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public final class OembedResponse implements Serializable {

	/**
	 * Constants for supported oembed formats.
	 */
	public enum Format {

		/**
		 * Constant for JSON.
		 */
		json,
		/**
		 * Constant for XML.
		 */
		xml

	}

	private static final long serialVersionUID = -2038373410581285921L;

	/**
	 * The resource type. Valid values, along with value-specific parameters, are
	 * described below.
	 */
	@XmlElement(name = "type")
	private String type;

	/**
	 * The oEmbed version number. This must be 1.0.
	 */
	@XmlElement(name = "version")
	private String version;

	/**
	 * A text title, describing the resource.
	 */
	@XmlElement(name = "title")
	private String title;

	/**
	 * The name of the author/owner of the resource.
	 */
	@XmlElement(name = "author_name")
	private String authorName;

	/**
	 * A URL for the author/owner of the resource.
	 */
	@XmlElement(name = "author_url")
	private String authorUrl;

	/**
	 * The name of the resource provider.
	 */
	@XmlElement(name = "provider_name")
	private String providerName;

	/**
	 * The url of the resource provider.
	 */
	@XmlElement(name = "provider_url")
	private String providerUrl;

	/**
	 * The suggested cache lifetime for this resource, in seconds. Consumers may choose to
	 * use this value or not.
	 */
	@XmlElement(name = "cache_age")
	private Long cacheAge;

	/**
	 * A URL to a thumbnail image representing the resource. The thumbnail must respect
	 * any {@code maxwidth} and {@code maxheight} parameters. If this parameter is
	 * present, {@code thumbnail_width} and {@code thumbnail_height} must also be present.
	 */
	@XmlElement(name = "thumbnail_url")
	private String thumbnailUrl;

	/**
	 * The width of the optional thumbnail. If this parameter is present,
	 * {@code thumbnail_url} and {@code thumbnail_height} must also be present.
	 */
	@XmlElement(name = "thumbnail_width")
	private Integer thumbnailWidth;

	/**
	 * The height of the optional thumbnail. If this parameter is present,
	 * {@code thumbnail_url} and {@code thumbnail_width} must also be present.
	 */
	@XmlElement(name = "thumbnail_height")
	private Integer thumbnailHeight;

	/**
	 * Required for type {@code photo}. The source URL of the image. Consumers should be
	 * able to insert this URL into an {@code <img>} element. Only HTTP and HTTPS URLs are
	 * valid.
	 */
	@XmlElement(name = "url")
	private String url;

	/**
	 * Required for type {@code video} and {@code rich}. The HTML required to embed a
	 * video player. The HTML should have no padding or margins. Consumers may wish to
	 * load the HTML in an off-domain iframe to avoid XSS vulnerabilities.
	 */
	@XmlElement(name = "html")
	private String html;

	/**
	 * The width in pixels of the image specified in the {@code url} parameter.
	 */
	@XmlElement(name = "width")
	private Integer width;

	/**
	 * The height in pixels of the image specified in the {@code url} parameter.
	 */
	@XmlElement(name = "height")
	private Integer height;

	public String getType() {
		return this.type;
	}

	public void setType(final String type) {
		this.type = type;
	}

	public String getVersion() {
		return this.version;
	}

	public void setVersion(final String version) {
		this.version = version;
	}

	public String getTitle() {
		return this.title;
	}

	public void setTitle(final String title) {
		this.title = title;
	}

	public String getAuthorName() {
		return this.authorName;
	}

	public void setAuthorName(final String authorName) {
		this.authorName = authorName;
	}

	public String getAuthorUrl() {
		return this.authorUrl;
	}

	public void setAuthorUrl(final String authorUrl) {
		this.authorUrl = authorUrl;
	}

	public String getProviderName() {
		return this.providerName;
	}

	public void setProviderName(final String providerName) {
		this.providerName = providerName;
	}

	public String getProviderUrl() {
		return this.providerUrl;
	}

	public void setProviderUrl(final String providerUrl) {
		this.providerUrl = providerUrl;
	}

	public Long getCacheAge() {
		return this.cacheAge;
	}

	public void setCacheAge(final Long cacheAge) {
		this.cacheAge = cacheAge;
	}

	public String getThumbnailUrl() {
		return this.thumbnailUrl;
	}

	public void setThumbnailUrl(final String thumbnailUrl) {
		this.thumbnailUrl = thumbnailUrl;
	}

	public Integer getThumbnailWidth() {
		return this.thumbnailWidth;
	}

	public void setThumbnailWidth(final Integer thumbnailWidth) {
		this.thumbnailWidth = thumbnailWidth;
	}

	public Integer getThumbnailHeight() {
		return this.thumbnailHeight;
	}

	public void setThumbnailHeight(final Integer thumbnailHeight) {
		this.thumbnailHeight = thumbnailHeight;
	}

	public String getUrl() {
		return this.url;
	}

	public void setUrl(final String url) {
		this.url = url;
	}

	public String getHtml() {
		return this.html;
	}

	public void setHtml(final String html) {
		this.html = html;
	}

	public Integer getWidth() {
		return this.width;
	}

	public void setWidth(final Integer width) {
		this.width = width;
	}

	public Integer getHeight() {
		return this.height;
	}

	public void setHeight(final Integer height) {
		this.height = height;
	}

}
