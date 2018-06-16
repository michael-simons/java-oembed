/*
 * Copyright 2015-2018 michael-simons.eu.
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
import java.util.Optional;

import org.apache.http.client.methods.HttpGet;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Michael J. Simons, 2015-01-01
 */
public class DefaultRequestProviderTest {

	@Test
	public void createRequestForShouldWork() throws URISyntaxException {
		final DefaultRequestProvider requestProvider = new DefaultRequestProvider();

		HttpGet request = requestProvider.createRequestFor("java-oembed2/4711", null, new URI("https://dailyfratze.de"));

		Assert.assertEquals("https://dailyfratze.de", request.getURI().toString());
		Assert.assertEquals("java-oembed2/4711", request.getFirstHeader("User-Agent").getValue());

		request = requestProvider.createRequestFor("java-oembed2/4711", "dailyfratze", new URI("https://dailyfratze.de"));

		Assert.assertEquals("https://dailyfratze.de", request.getURI().toString());
		Assert.assertEquals("java-oembed2/4711; dailyfratze", request.getFirstHeader("User-Agent").getValue());
	}
}
