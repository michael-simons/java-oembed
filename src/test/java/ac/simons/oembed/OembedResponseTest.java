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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author Michael J. Simons, 2014-12-28
 */
public class OembedResponseTest {

    @Test
    public void beanShouldWorkAsExpected() {
	final OembedResponse oembedResponse = new OembedResponse();
	Assertions.assertNull(oembedResponse.getType());
	Assertions.assertNull(oembedResponse.getVersion());
	Assertions.assertNull(oembedResponse.getTitle());
	Assertions.assertNull(oembedResponse.getAuthorName());
	Assertions.assertNull(oembedResponse.getAuthorUrl());
	Assertions.assertNull(oembedResponse.getProviderName());
	Assertions.assertNull(oembedResponse.getProviderUrl());
	Assertions.assertNull(oembedResponse.getCacheAge());
	Assertions.assertNull(oembedResponse.getThumbnailUrl());
	Assertions.assertNull(oembedResponse.getThumbnailWidth());
	Assertions.assertNull(oembedResponse.getThumbnailHeight());
	Assertions.assertNull(oembedResponse.getUrl());
	Assertions.assertNull(oembedResponse.getHtml());
	Assertions.assertNull(oembedResponse.getWidth());
	Assertions.assertNull(oembedResponse.getHeight());

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

	Assertions.assertEquals("type", oembedResponse.getType());
	Assertions.assertEquals("version", oembedResponse.getVersion());
	Assertions.assertEquals("title", oembedResponse.getTitle());
	Assertions.assertEquals("authorName", oembedResponse.getAuthorName());
	Assertions.assertEquals("authorUrl", oembedResponse.getAuthorUrl());
	Assertions.assertEquals("providerName", oembedResponse.getProviderName());
	Assertions.assertEquals("providerUrl", oembedResponse.getProviderUrl());
	Assertions.assertEquals(Long.valueOf(4711l), oembedResponse.getCacheAge());
	Assertions.assertEquals("thumbnailUrl", oembedResponse.getThumbnailUrl());
	Assertions.assertEquals(Integer.valueOf(23), oembedResponse.getThumbnailWidth());
	Assertions.assertEquals(Integer.valueOf(42), oembedResponse.getThumbnailHeight());
	Assertions.assertEquals("url", oembedResponse.getUrl());
	Assertions.assertEquals("html", oembedResponse.getHtml());
	Assertions.assertEquals(Integer.valueOf(44), oembedResponse.getWidth());
	Assertions.assertEquals(Integer.valueOf(55), oembedResponse.getHeight());
    }
}
