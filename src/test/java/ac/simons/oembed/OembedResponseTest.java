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

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Michael J. Simons, 2014-12-28
 */
public class OembedResponseTest {

    @Test
    public void beanShouldWorkAsExpected() {
	final OembedResponse oembedResponse = new OembedResponse();
	Assert.assertNull(oembedResponse.getType());
	Assert.assertNull(oembedResponse.getVersion());
	Assert.assertNull(oembedResponse.getTitle());
	Assert.assertNull(oembedResponse.getAuthorName());
	Assert.assertNull(oembedResponse.getAuthorUrl());
	Assert.assertNull(oembedResponse.getProviderName());
	Assert.assertNull(oembedResponse.getProviderUrl());
	Assert.assertNull(oembedResponse.getCacheAge());
	Assert.assertNull(oembedResponse.getThumbnailUrl());
	Assert.assertNull(oembedResponse.getThumbnailWidth());
	Assert.assertNull(oembedResponse.getThumbnailHeight());
	Assert.assertNull(oembedResponse.getUrl());
	Assert.assertNull(oembedResponse.getHtml());
	Assert.assertNull(oembedResponse.getWidth());
	Assert.assertNull(oembedResponse.getHeight());

	oembedResponse.setType("type");
	oembedResponse.setVersion("version");
	oembedResponse.setTitle("title");
	oembedResponse.setAuthorName("authorName");
	oembedResponse.setAuthorUrl("authorUrl");
	oembedResponse.setProviderName("providerName");
	oembedResponse.setProviderUrl("providerUrl");
	oembedResponse.setCacheAge(4711l);
	oembedResponse.setThumbnailUrl("thumbnailUrl");
	oembedResponse.setThumbnailWidth(23);
	oembedResponse.setThumbnailHeight(42);
	oembedResponse.setUrl("url");
	oembedResponse.setHtml("html");
	oembedResponse.setWidth(44);
	oembedResponse.setHeight(55);

	Assert.assertEquals("type", oembedResponse.getType());
	Assert.assertEquals("version", oembedResponse.getVersion());
	Assert.assertEquals("title", oembedResponse.getTitle());
	Assert.assertEquals("authorName", oembedResponse.getAuthorName());
	Assert.assertEquals("authorUrl", oembedResponse.getAuthorUrl());
	Assert.assertEquals("providerName", oembedResponse.getProviderName());
	Assert.assertEquals("providerUrl", oembedResponse.getProviderUrl());
	Assert.assertEquals(new Long(4711l), oembedResponse.getCacheAge());
	Assert.assertEquals("thumbnailUrl", oembedResponse.getThumbnailUrl());
	Assert.assertEquals(new Integer(23), oembedResponse.getThumbnailWidth());
	Assert.assertEquals(new Integer(42), oembedResponse.getThumbnailHeight());
	Assert.assertEquals("url", oembedResponse.getUrl());
	Assert.assertEquals("html", oembedResponse.getHtml());
	Assert.assertEquals(new Integer(44), oembedResponse.getWidth());
	Assert.assertEquals(new Integer(55), oembedResponse.getHeight());
    }
}
