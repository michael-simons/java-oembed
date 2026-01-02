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

import java.util.ArrayList;
import java.util.HashMap;

import ac.simons.oembed.OembedResponse.Format;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

/**
 * @author Michael J. Simons
 * @since 2014-12-30
 */
public class OembedEndpointTests {

	@Test
	public void beanShouldWorkAsExpected() {
		final OembedEndpoint oembedEndpoint = new OembedEndpoint();

		assertThat(oembedEndpoint.getName()).isNull();
		assertThat(oembedEndpoint.getEndpoint()).isNull();
		assertThat(oembedEndpoint.getFormat()).isEqualTo(Format.json);
		assertThat(oembedEndpoint.getMaxWidth()).isNull();
		assertThat(oembedEndpoint.getMaxHeight()).isNull();
		assertThat(oembedEndpoint.getUrlSchemes()).isNull();
		assertThat(oembedEndpoint.getRequestProviderClass()).isEqualTo(DefaultRequestProvider.class);
		assertThat(oembedEndpoint.getRequestProviderProperties()).isNull();
		assertThat(oembedEndpoint.getResponseRendererClass()).isEqualTo(DefaultOembedResponseRenderer.class);
		assertThat(oembedEndpoint.getResponseRendererProperties()).isNull();

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

		assertThat(oembedEndpoint.getName()).isEqualTo("name");
		assertThat(oembedEndpoint.getEndpoint()).isEqualTo("endpoint");
		assertThat(oembedEndpoint.getFormat()).isEqualTo(Format.xml);
		assertThat(oembedEndpoint.getMaxWidth()).isEqualTo(Integer.valueOf(4711));
		assertThat(oembedEndpoint.getMaxHeight()).isEqualTo(Integer.valueOf(23));
		assertThat(oembedEndpoint.getUrlSchemes()).isEqualTo(new ArrayList<>());
		assertThat(oembedEndpoint.getRequestProviderClass()).isEqualTo(DummyRequestProvider.class);
		assertThat(oembedEndpoint.getRequestProviderProperties()).isEqualTo(new HashMap<>());
		assertThat(oembedEndpoint.getResponseRendererClass()).isEqualTo(DummyRenderer.class);
		assertThat(oembedEndpoint.getResponseRendererProperties()).isEqualTo(new HashMap<>());
	}

	@Test
	public void toApiUrlShouldWork() {
		OembedEndpoint oembedEndpoint = new OembedEndpoint();

		oembedEndpoint.setEndpoint("https://biking.michael-simons.eu/oembed");
		oembedEndpoint.setFormat(Format.json);
		oembedEndpoint.setMaxWidth(480);
		oembedEndpoint.setMaxHeight(360);

		assertThat(oembedEndpoint.toApiUrl("https://biking.michael-simons.eu/tracks/1")).hasToString(
				"https://biking.michael-simons.eu/oembed?format=json&url=https%3A%2F%2Fbiking.michael-simons.eu%2Ftracks%2F1&maxwidth=480&maxheight=360");

		oembedEndpoint.setEndpoint("https://api.twitter.com/1.1/statuses/oembed.%{format}");
		oembedEndpoint.setFormat(Format.json);
		oembedEndpoint.setMaxWidth(null);
		oembedEndpoint.setMaxHeight(null);

		assertThat(oembedEndpoint.toApiUrl("https://twitter.com/rotnroll666/status/549898095853838336")).hasToString(
				"https://api.twitter.com/1.1/statuses/oembed.json?url=https%3A%2F%2Ftwitter.com%2Frotnroll666%2Fstatus%2F549898095853838336");
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

		assertThatExceptionOfType(OembedException.class).isThrownBy(() -> oembedEndpoint.toApiUrl("---"))
			.withMessage("Expected scheme name at index 0: :foobar:/test.de");
	}

}
