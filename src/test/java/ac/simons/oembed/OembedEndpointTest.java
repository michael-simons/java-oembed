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

import ac.simons.oembed.OembedResponse.Format;
import java.util.ArrayList;
import java.util.HashMap;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * @author Michael J. Simons, 2014-12-30
 */
public class OembedEndpointTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void beanShouldWorkAsExpected() {
	final OembedEndpoint oembedEndpoint = new OembedEndpoint();

	Assert.assertNull(oembedEndpoint.getName());
	Assert.assertNull(oembedEndpoint.getEndpoint());
	Assert.assertEquals(Format.json, oembedEndpoint.getFormat());
	Assert.assertNull(oembedEndpoint.getMaxWidth());
	Assert.assertNull(oembedEndpoint.getMaxHeight());
	Assert.assertNull(oembedEndpoint.getUrlSchemes());
	Assert.assertEquals(DefaultRequestProvider.class, oembedEndpoint.getRequestProviderClass());
	Assert.assertNull(oembedEndpoint.getRequestProviderProperties());
	Assert.assertEquals(DefaultOembedResponseRenderer.class, oembedEndpoint.getResponseRendererClass());
	Assert.assertNull(oembedEndpoint.getResponseRendererProperties());

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

	Assert.assertEquals("name", oembedEndpoint.getName());
	Assert.assertEquals("endpoint", oembedEndpoint.getEndpoint());
	Assert.assertEquals(Format.xml, oembedEndpoint.getFormat());
	Assert.assertEquals(new Integer(4711), oembedEndpoint.getMaxWidth());
	Assert.assertEquals(new Integer(23), oembedEndpoint.getMaxHeight());
	Assert.assertEquals(new ArrayList<>(), oembedEndpoint.getUrlSchemes());
	Assert.assertEquals(DummyRequestProvider.class, oembedEndpoint.getRequestProviderClass());
	Assert.assertEquals(new HashMap<>(), oembedEndpoint.getRequestProviderProperties());
	Assert.assertEquals(DummyRenderer.class, oembedEndpoint.getResponseRendererClass());
	Assert.assertEquals(new HashMap<>(), oembedEndpoint.getResponseRendererProperties());
    }

    @Test
    public void toApiUrlShouldWork() {
	OembedEndpoint oembedEndpoint = new OembedEndpoint();

	oembedEndpoint.setEndpoint("http://biking.michael-simons.eu/oembed");
	oembedEndpoint.setFormat(Format.json);
	oembedEndpoint.setMaxWidth(480);
	oembedEndpoint.setMaxHeight(360);

	Assert.assertEquals("http://biking.michael-simons.eu/oembed?format=json&url=http%3A%2F%2Fbiking.michael-simons.eu%2Ftracks%2F1&maxwidth=480&maxheight=360", oembedEndpoint.toApiUrl("http://biking.michael-simons.eu/tracks/1").toString());

	oembedEndpoint.setEndpoint("https://api.twitter.com/1.1/statuses/oembed.%{format}");
	oembedEndpoint.setFormat(Format.json);
	oembedEndpoint.setMaxWidth(null);
	oembedEndpoint.setMaxHeight(null);

	Assert.assertEquals("https://api.twitter.com/1.1/statuses/oembed.json?url=https%3A%2F%2Ftwitter.com%2Frotnroll666%2Fstatus%2F549898095853838336", oembedEndpoint.toApiUrl("https://twitter.com/rotnroll666/status/549898095853838336").toString());
    }

    @Test
    public void toApiUrlShouldWork2() {
	expectedException.expect(OembedException.class);
	expectedException.expectMessage("Expected scheme name at index 0: :foobar:/test.de");

	final OembedEndpoint oembedEndpoint = new OembedEndpoint();

	oembedEndpoint.setName("name");
	oembedEndpoint.setEndpoint(":foobar:/test.de");
	oembedEndpoint.setFormat(Format.json);
	oembedEndpoint.setMaxWidth(4711);
	oembedEndpoint.setMaxHeight(23);
	oembedEndpoint.setUrlSchemes(new ArrayList<>());

	oembedEndpoint.toApiUrl("---");
    }

}
