/*
 * Copyright 2015 michael-simons.eu.
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
import java.net.URI;
import java.net.URISyntaxException;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Michael J. Simons, 2015-01-09
 */
public class AutodiscoveredOembedEndpointTest {

    @Test
    public void autodiscoveredOembedEndpointShouldWorkAsExpected() throws URISyntaxException {
	final AutodiscoveredOembedEndpoint endpoint = new AutodiscoveredOembedEndpoint(new URI("http://foobar"), Format.xml);
	Assert.assertEquals(Format.xml, endpoint.getFormat());
	Assert.assertEquals(new URI("http://foobar"), endpoint.toApiUrl("http://heise.de"));
	Assert.assertEquals(new URI("http://foobar"), endpoint.toApiUrl("http://xxx.de"));
	endpoint.setEndpoint("http://biking.michael-simons.eu");
	endpoint.setFormat(Format.json);
	Assert.assertEquals(new URI("http://foobar"), endpoint.toApiUrl("http://heise.de"));
	Assert.assertEquals(Format.xml, endpoint.getFormat());
    }
}
