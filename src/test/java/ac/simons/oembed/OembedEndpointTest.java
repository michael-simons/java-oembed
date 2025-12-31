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
import java.util.ArrayList;
import java.util.HashMap;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author Michael J. Simons
 * @since 2014-12-30
 */
public class OembedEndpointTest {

    @Test
    public void beanShouldWorkAsExpected() {
	final OembedEndpoint oembedEndpoint = new OembedEndpoint();

	Assertions.assertNull(oembedEndpoint.getName());
	Assertions.assertNull(oembedEndpoint.getEndpoint());
	Assertions.assertEquals(Format.json, oembedEndpoint.getFormat());
	Assertions.assertNull(oembedEndpoint.getMaxWidth());
	Assertions.assertNull(oembedEndpoint.getMaxHeight());
	Assertions.assertNull(oembedEndpoint.getUrlSchemes());
	Assertions.assertEquals(DefaultRequestProvider.class, oembedEndpoint.getRequestProviderClass());
	Assertions.assertNull(oembedEndpoint.getRequestProviderProperties());
	Assertions.assertEquals(DefaultOembedResponseRenderer.class, oembedEndpoint.getResponseRendererClass());
	Assertions.assertNull(oembedEndpoint.getResponseRendererProperties());

	oembedEndpoint.setName("name");
	oembedEndpoint.setEndpoint("endpoint");
	oembedEndpoint.setFormat(Format.xml);
	oembedEndpoint.setMaxWidth(4711);
	oembedEndpoint.setMaxHeight(23);
	oembedEndpoint.setUrlSchemes(new ArrayList<>());
	oembedEndpoint.setRequestProviderClass(DummyRequestProvider.class);
	oembedEndpoint.setRequestProviderProperties(new HashMap<>());
	oembedEndpoint.setResponseRendererClass(DummyRenderer.class);
	oembedEndpoint.setResponseRendererProperties(new HashMap<>());

	Assertions.assertEquals("name", oembedEndpoint.getName());
	Assertions.assertEquals("endpoint", oembedEndpoint.getEndpoint());
	Assertions.assertEquals(Format.xml, oembedEndpoint.getFormat());
	Assertions.assertEquals(Integer.valueOf(4711), oembedEndpoint.getMaxWidth());
	Assertions.assertEquals(Integer.valueOf(23), oembedEndpoint.getMaxHeight());
	Assertions.assertEquals(new ArrayList<>(), oembedEndpoint.getUrlSchemes());
	Assertions.assertEquals(DummyRequestProvider.class, oembedEndpoint.getRequestProviderClass());
	Assertions.assertEquals(new HashMap<>(), oembedEndpoint.getRequestProviderProperties());
	Assertions.assertEquals(DummyRenderer.class, oembedEndpoint.getResponseRendererClass());
	Assertions.assertEquals(new HashMap<>(), oembedEndpoint.getResponseRendererProperties());
    }

    @Test
    public void toApiUrlShouldWork() {
	OembedEndpoint oembedEndpoint = new OembedEndpoint();

	oembedEndpoint.setEndpoint("https://biking.michael-simons.eu/oembed");
	oembedEndpoint.setFormat(Format.json);
	oembedEndpoint.setMaxWidth(480);
	oembedEndpoint.setMaxHeight(360);

	Assertions.assertEquals("https://biking.michael-simons.eu/oembed?format=json&url=https%3A%2F%2Fbiking.michael-simons.eu%2Ftracks%2F1&maxwidth=480&maxheight=360", oembedEndpoint.toApiUrl("https://biking.michael-simons.eu/tracks/1").toString());

	oembedEndpoint.setEndpoint("https://api.twitter.com/1.1/statuses/oembed.%{format}");
	oembedEndpoint.setFormat(Format.json);
	oembedEndpoint.setMaxWidth(null);
	oembedEndpoint.setMaxHeight(null);

	Assertions.assertEquals("https://api.twitter.com/1.1/statuses/oembed.json?url=https%3A%2F%2Ftwitter.com%2Frotnroll666%2Fstatus%2F549898095853838336", oembedEndpoint.toApiUrl("https://twitter.com/rotnroll666/status/549898095853838336").toString());
    }

    @Test
    public void toApiUrlShouldWork2() {

	final OembedEndpoint oembedEndpoint = new OembedEndpoint();

	oembedEndpoint.setName("name");
	oembedEndpoint.setEndpoint(":foobar:/test.de");
	oembedEndpoint.setFormat(Format.json);
	oembedEndpoint.setMaxWidth(4711);
	oembedEndpoint.setMaxHeight(23);
	oembedEndpoint.setUrlSchemes(new ArrayList<>());

	Assertions.assertThrowsExactly(OembedException.class, () ->
	oembedEndpoint.toApiUrl("---"), "Expected scheme name at index 0: :foobar:/test.de");
    }

}
