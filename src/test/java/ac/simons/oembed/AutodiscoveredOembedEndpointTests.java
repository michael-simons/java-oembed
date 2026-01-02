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

import java.net.URI;
import java.net.URISyntaxException;

import ac.simons.oembed.OembedResponse.Format;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Michael J. Simons, 2015-01-09
 */
public class AutodiscoveredOembedEndpointTests {

	@Test
	public void autodiscoveredOembedEndpointShouldWorkAsExpected() throws URISyntaxException {
		final AutodiscoveredOembedEndpoint endpoint = new AutodiscoveredOembedEndpoint(new URI("http://foobar"),
				Format.xml);
		assertThat(endpoint.getFormat()).isEqualTo(Format.xml);
		assertThat(endpoint.toApiUrl("http://heise.de")).isEqualTo(new URI("http://foobar"));
		assertThat(endpoint.toApiUrl("http://xxx.de")).isEqualTo(new URI("http://foobar"));
		endpoint.setEndpoint("http://biking.michael-simons.eu");
		endpoint.setFormat(Format.json);
		assertThat(endpoint.toApiUrl("http://heise.de")).isEqualTo(new URI("http://foobar"));
		assertThat(endpoint.getFormat()).isEqualTo(Format.xml);
	}

}
