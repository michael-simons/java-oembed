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

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;

/**
 * @author Michael J. Simons
 */
@XmlRootElement(name="oembed")
@XmlAccessorType(XmlAccessType.FIELD)
@JsonSerialize(include=Inclusion.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown=true)
public class OembedResponse implements Serializable {
	private static final long serialVersionUID = -1965788850835022977L;

	/** This one is not mapped to json and xml but set right after parsing. The source equals the provider name */	
	@javax.xml.bind.annotation.XmlTransient
	private String source;
	/** This is the original url which got transformed */
	@javax.xml.bind.annotation.XmlTransient
	private String originalUrl;
	@javax.xml.bind.annotation.XmlTransient
	private boolean empty = false;
	
	@XmlElement(name="type")
	private String type;
	@XmlElement(name="version")
	private String version;
	@XmlElement(name="title")
	private String title;
	@XmlElement(name="author_name")
	private String authorName;
	@XmlElement(name="author_url")
	private String authorUrl;
	@XmlElement(name="provider_name")
	private String providerName;
	@XmlElement(name="provider_url")
	private String providerUrl;
	@XmlElement(name="cache_age")
	private Long cacheAge;
	@XmlElement(name="thumbnail_url")
	private String thumbnailUrl;
	@XmlElement(name="thumbnail_width")
	private Integer thumbnailWidth;
	@XmlElement(name="thumbnail_height")
	private Integer thumbnailHeight;
	@XmlElement(name="url")
	private String url;
	@XmlElement(name="html")
	private String html;
	@XmlElement(name="width")
	private Integer width;
	@XmlElement(name="height")
	private Integer height;

	/**
	 * @param possibleSource
	 * @return True if this source is given and equals <code>possibleSource</code>
	 */
	public boolean from(String possibleSource) {
		return this.getSource() != null && this.source.equals(possibleSource);
	}
		
	public String getSource() {
		return source;
	}

	public void setSource(String src) {
		this.source = src;
	}
	
	public String getOriginalUrl() {
		return originalUrl;
	}

	public void setOriginalUrl(String originalUrl) {
		this.originalUrl = originalUrl;
	}
	
	public String getType() {
		return type;
	}
	
	public void setType(String type) {
		this.type = type;
	}
		
	public String getVersion() {
		return version;
	}
	
	public void setVersion(String version) {
		this.version = version;
	}
		
	public String getTitle() {
		return title;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
		
	public String getAuthorName() {
		return authorName;
	}
	
	public void setAuthorName(String authorName) {
		this.authorName = authorName;
	}
	
	public String getAuthorUrl() {
		return authorUrl;
	}
	
	public void setAuthorUrl(String authorUrl) {
		this.authorUrl = authorUrl;
	}
	
	public String getProviderName() {
		return providerName;
	}
	
	public void setProviderName(String providerName) {
		this.providerName = providerName;
	}
		
	public String getProviderUrl() {
		return providerUrl;
	}
	
	public void setProviderUrl(String providerUrl) {
		this.providerUrl = providerUrl;
	}
		
	public Long getCacheAge() {
		return cacheAge;
	}
	
	public void setCacheAge(Long cacheAge) {
		this.cacheAge = cacheAge;
	}
		
	public String getThumbnailUrl() {
		return thumbnailUrl;
	}
	
	public void setThumbnailUrl(String thumbnailUrl) {
		this.thumbnailUrl = thumbnailUrl;
	}
		
	public Integer getThumbnailWidth() {
		return thumbnailWidth;
	}
	
	public void setThumbnailWidth(Integer thumbnailWidth) {
		this.thumbnailWidth = thumbnailWidth;
	}
		
	public Integer getThumbnailHeight() {
		return thumbnailHeight;
	}
	
	public void setThumbnailHeight(Integer thumbnailHeight) {
		this.thumbnailHeight = thumbnailHeight;
	}
	
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
	
	public String getHtml() {
		return html;
	}

	public void setHtml(String html) {
		this.html = html;
	}
	
	public Integer getWidth() {
		return width;
	}

	public void setWidth(Integer width) {
		this.width = width;
	}
	
	public Integer getHeight() {
		return height;
	}

	public void setHeight(Integer height) {
		this.height = height;
	}

	@Override
	public String toString() {
		return "OembedResponse [type=" + type + ", version=" + version
				+ ", title=" + title + ", authorName=" + authorName
				+ ", authorUrl=" + authorUrl + ", providerName=" + providerName
				+ ", providerUrl=" + providerUrl + ", cacheAge=" + cacheAge
				+ ", thumbnailUrl=" + thumbnailUrl + ", thumbnailWidth="
				+ thumbnailWidth + ", thumbnailHeight=" + thumbnailHeight
				+ ", url=" + url + ", html=" + html + ", width=" + width
				+ ", height=" + height + "]";
	}
	
	/**
	 * Renders a html representation of this oembed response
	 * @return
	 */
	public String render() {
		String rv = null;
		if(this.getType().equalsIgnoreCase("photo"))
			rv = this.renderPhoto();
		else if(this.getType().equalsIgnoreCase("video"))
			rv = this.renderVideo();
		else if(this.getType().equalsIgnoreCase("link"))
			rv = this.renderLink();
		else if(this.getType().equalsIgnoreCase("rich"))
			rv = this.renderRich();
		return rv;
	}
	
	private String renderVideo() {
		return this.getHtml();
	}
	
	private String renderPhoto() {
		final String _title = this.getTitle() == null ? "" : this.getTitle();
		return String.format("<img src=\"%s\" style=\"width:%dpx; height:%dpx;\" alt=\"%s\" title=\"%s\"/>", this.getUrl(), this.getWidth(), this.getHeight(), _title, _title);
	}
	
	private String renderLink() {
		final String _title = this.getTitle() == null ? this.getOriginalUrl() : this.getTitle();
		final String _url = this.getUrl() == null ? this.getOriginalUrl() : this.getUrl();
		return String.format("<a href=\"%s\">%s</a>", _url, _title);
	}
	
	private String renderRich() {
		return this.getHtml();
	}

	public boolean isEmpty() {
		return empty;
	}

	public void setEmpty(boolean empty) {
		this.empty = empty;
	}	
}