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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.introspect.AnnotationIntrospectorPair;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationIntrospector;

/**
 * Provides JSON Parsing for {@link OembedResponse}s. This class uses a private
 * {@link ObjectMapper} to ensure that the JAXB annotation introspector is configured
 * correctly.
 *
 * @author Michael J. Simons, 2010-12-24
 */
public final class OembedJsonParser implements OembedParser {

	/**
	 * Private instance of an object mapper with JaxbAnnotationIntrospector configured.
	 */
	private final ObjectMapper objectMapper;

	/**
	 * Creates a new OembedJsonParser.
	 */
	public OembedJsonParser() {
		this.objectMapper = new ObjectMapper()
			.setAnnotationIntrospector(new AnnotationIntrospectorPair(new JacksonAnnotationIntrospector(),
					new JaxbAnnotationIntrospector(TypeFactory.defaultInstance())))
			.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
	}

	@Override
	public OembedResponse unmarshal(final InputStream in) {
		try {
			return this.objectMapper.readValue(in, OembedResponse.class);
		}
		catch (IOException ex) {
			throw new OembedException(ex);
		}
	}

	@Override
	public void marshal(final OembedResponse oembedResponse, final OutputStream out) {
		try {
			this.objectMapper.writeValue(out, oembedResponse);
		}
		catch (IOException ex) {
			throw new OembedException(ex);
		}
	}

}
