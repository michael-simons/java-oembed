/*
 * Created by Michael Simons, michael-simons.eu
 * and released under The BSD License
 * http://www.opensource.org/licenses/bsd-license.php
 *
 * Copyright (c) 2010-2026, Michael Simons
 * All rights reserved.
 *
 * Redistribution  and  use  in  source   and  binary  forms,  with  or   without
 * modification, are permitted provided that the following conditions are met:
 *
 * * Redistributions of source   code must retain   the above copyright   notice,
 *   this list of conditions and the following disclaimer.
 *
 * * Redistributions in binary  form must reproduce  the above copyright  notice,
 *   this list of conditions  and the following  disclaimer in the  documentation
 *   and/or other materials provided with the distribution.
 *
 * * Neither the name  of  michael-simons.eu   nor the names  of its contributors
 *   may be used  to endorse   or promote  products derived  from  this  software
 *   without specific prior written permission.
 *
 * THIS SOFTWARE IS  PROVIDED BY THE  COPYRIGHT HOLDERS AND  CONTRIBUTORS "AS IS"
 * AND ANY  EXPRESS OR  IMPLIED WARRANTIES,  INCLUDING, BUT  NOT LIMITED  TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL  THE COPYRIGHT HOLDER OR CONTRIBUTORS  BE LIABLE
 * FOR ANY  DIRECT, INDIRECT,  INCIDENTAL, SPECIAL,  EXEMPLARY, OR  CONSEQUENTIAL
 * DAMAGES (INCLUDING,  BUT NOT  LIMITED TO,  PROCUREMENT OF  SUBSTITUTE GOODS OR
 * SERVICES; LOSS  OF USE,  DATA, OR  PROFITS; OR  BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT  LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE  USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package ac.simons.oembed;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

/**
 * @author Michael J. Simons
 * @since 2014-12-28
 */
public class OembedXmlParserTests {

	@Test
	public void unmarshallingShouldWork() {
		final String responseString = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><oembed><type>rich</type><version>1.0</version><title>Aachen - Maastricht - Aachen</title><author_name>Michael J. Simons</author_name><author_url>https://michael-simons.eu</author_url><provider_name>biking2</provider_name><provider_url>https://biking.michael-simons.eu</provider_url><cache_age>86400</cache_age><html>&lt;iframe width='1024' height='576' src='https://biking.michael-simons.eu/tracks/1/embed?width=1024&amp;height=576' class='bikingTrack'&gt;&lt;/iframe&gt;</html></oembed>";
		final OembedResponse response = new OembedXmlParser()
			.unmarshal(new ByteArrayInputStream(responseString.getBytes()));
		assertThat(response.getAuthorName()).isEqualTo("Michael J. Simons");
		assertThat(response.getAuthorUrl()).isEqualTo("https://michael-simons.eu");
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
		final OembedXmlParser parser = new OembedXmlParser();
		final OembedResponse oembedResponse = new OembedResponse();
		parser.marshal(oembedResponse, out);
		out.flush();
		out.close();
		assertThat(out).hasToString("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><oembed/>");

		out = new ByteArrayOutputStream();
		oembedResponse.setVersion("1.0");
		parser.marshal(oembedResponse, out);
		out.flush();
		out.close();
		assertThat(out).hasToString(
				"<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><oembed><version>1.0</version></oembed>");
	}

	@Test
	public void handleExceptions1() {

		final String responseString = "foobar";
		var oembedXmlParser = new OembedXmlParser();
		var in = new ByteArrayInputStream(responseString.getBytes());
		assertThatExceptionOfType(OembedException.class).isThrownBy(() -> oembedXmlParser.unmarshal(in));
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
