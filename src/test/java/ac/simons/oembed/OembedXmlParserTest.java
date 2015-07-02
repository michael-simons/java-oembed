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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 *
 * @author Michael J. Simons, 2014-12-28
 */
public class OembedXmlParserTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void unmarshallingShouldWork() throws IOException {
	final String responseString = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><oembed><type>rich</type><version>1.0</version><title>Aachen - Maastricht - Aachen</title><author_name>Michael J. Simons</author_name><author_url>http://michael-simons.eu</author_url><provider_name>biking2</provider_name><provider_url>http://biking.michael-simons.eu</provider_url><cache_age>86400</cache_age><html>&lt;iframe width='1024' height='576' src='http://biking.michael-simons.eu/tracks/1/embed?width=1024&amp;height=576' class='bikingTrack'&gt;&lt;/iframe&gt;</html></oembed>";
	final OembedResponse response = new OembedXmlParser().unmarshal(new ByteArrayInputStream(responseString.getBytes()));
	Assert.assertEquals("Michael J. Simons", response.getAuthorName());
	Assert.assertEquals("http://michael-simons.eu", response.getAuthorUrl());
	Assert.assertEquals(new Long(86400l), response.getCacheAge());
	Assert.assertEquals("<iframe width='1024' height='576' src='http://biking.michael-simons.eu/tracks/1/embed?width=1024&height=576' class='bikingTrack'></iframe>", response.getHtml());
	Assert.assertEquals("biking2", response.getProviderName());
	Assert.assertEquals("http://biking.michael-simons.eu", response.getProviderUrl());
	Assert.assertEquals("Aachen - Maastricht - Aachen", response.getTitle());
	Assert.assertEquals("rich", response.getType());
	Assert.assertEquals("1.0", response.getVersion());
    }

    @Test
    public void marshallingShouldWork() throws IOException {
	ByteArrayOutputStream out = new ByteArrayOutputStream();
	final OembedXmlParser parser = new OembedXmlParser();
	final OembedResponse oembedResponse = new OembedResponse();
	parser.marshal(oembedResponse, out);
	out.flush();
	out.close();
	Assert.assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><oembed/>", new String(out.toByteArray()));

	out = new ByteArrayOutputStream();
	oembedResponse.setVersion("1.0");
	parser.marshal(oembedResponse, out);
	out.flush();
	out.close();
	Assert.assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><oembed><version>1.0</version></oembed>", new String(out.toByteArray()));
    }

    @Test
    public void handleExceptions1() throws IOException {
	expectedException.expect(OembedException.class);	
	
	final String responseString = "foobar";
	final OembedResponse response = new OembedXmlParser().unmarshal(new ByteArrayInputStream(responseString.getBytes()));		
    }

    @Test
    public void handleExceptions2() throws IOException {
	final OutputStream out = new OutputStream() {

	    @Override
	    public void write(int b) throws IOException {
		throw new IOException("foobar");
	    }
	};

	expectedException.expect(OembedException.class);
	expectedException.expectMessage("foobar");

	new OembedJsonParser().marshal(new OembedResponse(), out);
    }
}