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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * This represents a valid OEmbed response according to the specs from
 * <a href="http://oembed.com">oembed.com</a>. In case anything breaks check the
 * endpoint in questions returns a valid response (types etc.).
 *
 * @author Michael J. Simons, 2010-12-24
 */
@XmlRootElement(name = "oembed")
@XmlAccessorType(XmlAccessType.FIELD)
@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class OembedResponse implements Serializable {

    /**
     * Constants for supported oembed formats.
     */
    public static enum Format {json, xml};
    
    private static final long serialVersionUID = -2038373410581285921L;

    /**
     * The resource type. Valid values, along with value-specific parameters,
     * are described below.
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
     * The suggested cache lifetime for this resource, in seconds. Consumers may
     * choose to use this value or not.
     */
    @XmlElement(name = "cache_age")
    private Long cacheAge;

    /**
     * A URL to a thumbnail image representing the resource. The thumbnail must
     * respect any {@code maxwidth} and {@code maxheight} parameters. If this
     * parameter is present, {@code thumbnail_width} and
     * {@code thumbnail_height} must also be present.
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
     * Required for type {@code photo}. The source URL of the image. Consumers
     * should be able to insert this URL into an {@code <img>} element. Only
     * HTTP and HTTPS URLs are valid.
     */
    @XmlElement(name = "url")
    private String url;

    /**
     * Required for type {@code video} and {@code rich}. The HTML required to
     * embed a video player. The HTML should have no padding or margins.
     * Consumers may wish to load the HTML in an off-domain iframe to avoid XSS
     * vulnerabilities.
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
}
