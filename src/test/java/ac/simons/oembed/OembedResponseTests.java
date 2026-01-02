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

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Michael J. Simons, 2014-12-28
 */
public class OembedResponseTests {

	@Test
	public void beanShouldWorkAsExpected() {
		final OembedResponse oembedResponse = new OembedResponse();
		assertThat(oembedResponse.getType()).isNull();
		assertThat(oembedResponse.getVersion()).isNull();
		assertThat(oembedResponse.getTitle()).isNull();
		assertThat(oembedResponse.getAuthorName()).isNull();
		assertThat(oembedResponse.getAuthorUrl()).isNull();
		assertThat(oembedResponse.getProviderName()).isNull();
		assertThat(oembedResponse.getProviderUrl()).isNull();
		assertThat(oembedResponse.getCacheAge()).isNull();
		assertThat(oembedResponse.getThumbnailUrl()).isNull();
		assertThat(oembedResponse.getThumbnailWidth()).isNull();
		assertThat(oembedResponse.getThumbnailHeight()).isNull();
		assertThat(oembedResponse.getUrl()).isNull();
		assertThat(oembedResponse.getHtml()).isNull();
		assertThat(oembedResponse.getWidth()).isNull();
		assertThat(oembedResponse.getHeight()).isNull();

		oembedResponse.setType("type");
		oembedResponse.setVersion("version");
		oembedResponse.setTitle("title");
		oembedResponse.setAuthorName("authorName");
		oembedResponse.setAuthorUrl("authorUrl");
		oembedResponse.setProviderName("providerName");
		oembedResponse.setProviderUrl("providerUrl");
		oembedResponse.setCacheAge(4711L);
		oembedResponse.setThumbnailUrl("thumbnailUrl");
		oembedResponse.setThumbnailWidth(23);
		oembedResponse.setThumbnailHeight(42);
		oembedResponse.setUrl("url");
		oembedResponse.setHtml("html");
		oembedResponse.setWidth(44);
		oembedResponse.setHeight(55);

		assertThat(oembedResponse.getType()).isEqualTo("type");
		assertThat(oembedResponse.getVersion()).isEqualTo("version");
		assertThat(oembedResponse.getTitle()).isEqualTo("title");
		assertThat(oembedResponse.getAuthorName()).isEqualTo("authorName");
		assertThat(oembedResponse.getAuthorUrl()).isEqualTo("authorUrl");
		assertThat(oembedResponse.getProviderName()).isEqualTo("providerName");
		assertThat(oembedResponse.getProviderUrl()).isEqualTo("providerUrl");
		assertThat(oembedResponse.getCacheAge()).isEqualTo(Long.valueOf(4711L));
		assertThat(oembedResponse.getThumbnailUrl()).isEqualTo("thumbnailUrl");
		assertThat(oembedResponse.getThumbnailWidth()).isEqualTo(Integer.valueOf(23));
		assertThat(oembedResponse.getThumbnailHeight()).isEqualTo(Integer.valueOf(42));
		assertThat(oembedResponse.getUrl()).isEqualTo("url");
		assertThat(oembedResponse.getHtml()).isEqualTo("html");
		assertThat(oembedResponse.getWidth()).isEqualTo(Integer.valueOf(44));
		assertThat(oembedResponse.getHeight()).isEqualTo(Integer.valueOf(55));
	}

}
