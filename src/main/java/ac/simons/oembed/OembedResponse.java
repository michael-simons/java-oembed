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

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Michael J. Simons
 */
@XmlRootElement(name="oembed")
public class OembedResponse implements Serializable {
	private static final long serialVersionUID = -1965788850835022977L;

	/** This one is not mapped to json and xml but set right after parsing. The source equals the provider name */
	private String source;
	/** This is the original url which got transformed */
	private String originalUrl;
	
	private String type;
	private String version;
	private String title;
	private String authorName;
	private String authorUrl;
	private String providerName;
	private String providerUrl;
	private Integer cacheAge;
	private String thumbnailUrl;
	private Integer thumbnailWidth;
	private Integer thumbnailHeight;
	private String url;
	private String html;
	private Integer width;
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

	@XmlElement(name="type")
	public String getType() {
		return type;
	}
	
	public void setType(String type) {
		this.type = type;
	}
	
	@XmlElement(name="version")
	public String getVersion() {
		return version;
	}
	
	public void setVersion(String version) {
		this.version = version;
	}
	
	@XmlElement(name="title")
	public String getTitle() {
		return title;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	@XmlElement(name="author_name")
	public String getAuthorName() {
		return authorName;
	}
	
	public void setAuthorName(String authorName) {
		this.authorName = authorName;
	}
	
	@XmlElement(name="author_url")
	public String getAuthorUrl() {
		return authorUrl;
	}
	
	public void setAuthorUrl(String authorUrl) {
		this.authorUrl = authorUrl;
	}
	
	@XmlElement(name="provider_name")
	public String getProviderName() {
		return providerName;
	}
	
	public void setProviderName(String providerName) {
		this.providerName = providerName;
	}
	
	@XmlElement(name="provider_url")
	public String getProviderUrl() {
		return providerUrl;
	}
	
	public void setProviderUrl(String providerUrl) {
		this.providerUrl = providerUrl;
	}
	
	@XmlElement(name="cache_age")
	public Integer getCacheAge() {
		return cacheAge;
	}
	
	public void setCacheAge(Integer cacheAge) {
		this.cacheAge = cacheAge;
	}
	
	@XmlElement(name="thumbnail_url")
	public String getThumbnailUrl() {
		return thumbnailUrl;
	}
	
	public void setThumbnailUrl(String thumbnailUrl) {
		this.thumbnailUrl = thumbnailUrl;
	}
	
	@XmlElement(name="thumbnail_width")
	public Integer getThumbnailWidth() {
		return thumbnailWidth;
	}
	
	public void setThumbnailWidth(Integer thumbnailWidth) {
		this.thumbnailWidth = thumbnailWidth;
	}
	
	@XmlElement(name="thumbnail_height")
	public Integer getThumbnailHeight() {
		return thumbnailHeight;
	}
	
	public void setThumbnailHeight(Integer thumbnailHeight) {
		this.thumbnailHeight = thumbnailHeight;
	}

	@XmlElement(name="url")
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	@XmlElement(name="html")
	public String getHtml() {
		return html;
	}

	public void setHtml(String html) {
		this.html = html;
	}

	@XmlElement(name="width")
	public Integer getWidth() {
		return width;
	}

	public void setWidth(Integer width) {
		this.width = width;
	}

	@XmlElement(name="height")
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
}