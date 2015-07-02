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

import java.io.InputStream;
import java.io.OutputStream;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

/**
 * Provides XML Parsing for {@link OembedResponse}s.
 *
 * @author Michael J. Simons, 2010-12-24
 */
class OembedXmlParser implements OembedParser {

    /**
     * The JAXB context for parsing XML Oembed responses
     */
    private final JAXBContext jaxbContext;

    /**
     * Creates a new OembedJsonParser. It can throw an exception if the JAXB
     * context cannot be initialized.
     */
    public OembedXmlParser() {
	try {
	    this.jaxbContext = JAXBContext.newInstance(OembedResponse.class);
	} catch (JAXBException ex) {
	    // Ignore this... I have no clue how that should happen.
	    throw new OembedException(ex);
	}
    }

    @Override
    public OembedResponse unmarshal(InputStream in) {
	try {
	    return (OembedResponse) jaxbContext.createUnmarshaller().unmarshal(in);
	} catch (JAXBException e) {	    
	    throw new OembedException(e);
	}
    }

    @Override
    public void marshal(OembedResponse oembedResponse, OutputStream out) {
	try {
	    jaxbContext.createMarshaller().marshal(oembedResponse, out);
	} catch (JAXBException e) {
	    throw new OembedException(e);
	}
    }
}
