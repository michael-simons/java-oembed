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
import java.io.OutputStream;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author Michael J. Simons
 * @since 2014-12-28
 */
public class OembedXmlParserTest {

    @Test
    public void unmarshallingShouldWork() throws IOException {
	final String responseString = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><oembed><type>rich</type><version>1.0</version><title>Aachen - Maastricht - Aachen</title><author_name>Michael J. Simons</author_name><author_url>http://michael-simons.eu</author_url><provider_name>biking2</provider_name><provider_url>https://biking.michael-simons.eu</provider_url><cache_age>86400</cache_age><html>&lt;iframe width='1024' height='576' src='https://biking.michael-simons.eu/tracks/1/embed?width=1024&amp;height=576' class='bikingTrack'&gt;&lt;/iframe&gt;</html></oembed>";
	final OembedResponse response = new OembedXmlParser().unmarshal(new ByteArrayInputStream(responseString.getBytes()));
	Assertions.assertEquals("Michael J. Simons", response.getAuthorName());
	Assertions.assertEquals("http://michael-simons.eu", response.getAuthorUrl());
	Assertions.assertEquals(Long.valueOf(86400l), response.getCacheAge());
	Assertions.assertEquals("<iframe width='1024' height='576' src='https://biking.michael-simons.eu/tracks/1/embed?width=1024&height=576' class='bikingTrack'></iframe>", response.getHtml());
	Assertions.assertEquals("biking2", response.getProviderName());
	Assertions.assertEquals("https://biking.michael-simons.eu", response.getProviderUrl());
	Assertions.assertEquals("Aachen - Maastricht - Aachen", response.getTitle());
	Assertions.assertEquals("rich", response.getType());
	Assertions.assertEquals("1.0", response.getVersion());
    }

    @Test
    public void marshallingShouldWork() throws IOException {
	ByteArrayOutputStream out = new ByteArrayOutputStream();
	final OembedXmlParser parser = new OembedXmlParser();
	final OembedResponse oembedResponse = new OembedResponse();
	parser.marshal(oembedResponse, out);
	out.flush();
	out.close();
	Assertions.assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><oembed/>", new String(out.toByteArray()));

	out = new ByteArrayOutputStream();
	oembedResponse.setVersion("1.0");
	parser.marshal(oembedResponse, out);
	out.flush();
	out.close();
	Assertions.assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><oembed><version>1.0</version></oembed>", new String(out.toByteArray()));
    }

    @Test
    public void handleExceptions1() throws IOException {

	final String responseString = "foobar";
	    var oembedXmlParser = new OembedXmlParser();
	    var in = new ByteArrayInputStream(responseString.getBytes());
		Assertions.assertThrowsExactly(OembedException.class, () -> oembedXmlParser.unmarshal(in));
    }

    @Test
    public void handleExceptions2() throws IOException {
	final OutputStream out = new OutputStream() {

	    @Override
	    public void write(int b) throws IOException {
		throw new IOException("foobar");
	    }
	};


	    var oembedJsonParser = new OembedJsonParser();
	    var oembedResponse = new OembedResponse();
	    Assertions.assertThrowsExactly(OembedException.class, () -> oembedJsonParser.marshal(oembedResponse, out), "foobar");
	    ;
    }
}