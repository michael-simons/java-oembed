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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

/**
 * @author Michael J. Simons
 * @since 2014-12-28
 */
public class OembedJsonParserTests {

	@Test
	public void unmarshallingShouldWork() throws IOException {
		final String responseString = "{\"author_name\":\"Michael J. Simons\",\"author_url\":\"http://michael-simons.eu\",\"cache_age\":86400,\"html\":\"<iframe width='1024' height='576' src='https://biking.michael-simons.eu/tracks/1/embed?width=1024&height=576' class='bikingTrack'></iframe>\",\"provider_name\":\"biking2\",\"provider_url\":\"https://biking.michael-simons.eu\",\"title\":\"Aachen - Maastricht - Aachen\",\"type\":\"rich\",\"version\":\"1.0\"}";
		final OembedResponse response = new OembedJsonParser()
			.unmarshal(new ByteArrayInputStream(responseString.getBytes()));
		assertThat(response.getAuthorName()).isEqualTo("Michael J. Simons");
		assertThat(response.getAuthorUrl()).isEqualTo("http://michael-simons.eu");
		assertThat(response.getCacheAge()).isEqualTo(Long.valueOf(86400L));
		assertThat(response.getHtml()).isEqualTo(
				"<iframe width='1024' height='576' src='https://biking.michael-simons.eu/tracks/1/embed?width=1024&height=576' class='bikingTrack'></iframe>");
		assertThat(response.getProviderName()).isEqualTo("biking2");
		assertThat(response.getProviderUrl()).isEqualTo("https://biking.michael-simons.eu");
		assertThat(response.getTitle()).isEqualTo("Aachen - Maastricht - Aachen");
		assertThat(response.getType()).isEqualTo("rich");
		assertThat(response.getVersion()).isEqualTo("1.0");
	}

	@Test
	public void marshallingShouldWork() throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		final OembedJsonParser oembedJsonParser = new OembedJsonParser();
		final OembedResponse oembedResponse = new OembedResponse();
		oembedJsonParser.marshal(oembedResponse, out);
		out.flush();
		out.close();
		assertThat(out).hasToString("{}");

		out = new ByteArrayOutputStream();
		oembedResponse.setVersion("1.0");
		oembedJsonParser.marshal(oembedResponse, out);
		out.flush();
		out.close();
		assertThat(out).hasToString("{\"version\":\"1.0\"}");
	}

	@Test
	public void handleExceptions1() {
		final InputStream in = new InputStream() {

			@Override
			public int read() throws IOException {
				throw new IOException("foobar");
			}
		};

		var oembedJsonParser = new OembedJsonParser();
		assertThatExceptionOfType(OembedException.class).isThrownBy(() -> oembedJsonParser.unmarshal(in))
			.withMessage("foobar");
	}

	@Test
	public void handleExceptions2() {
		final OutputStream out = new OutputStream() {

			@Override
			public void write(int b) throws IOException {
				throw new IOException("foobar");
			}
		};

		var oembedJsonParser = new OembedJsonParser();
		var oembedResponse = new OembedResponse();
		assertThatExceptionOfType(OembedException.class).isThrownBy(() -> oembedJsonParser.marshal(oembedResponse, out))
			.withMessage("foobar");

	}

}
