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

/**
 * An exception for wrapping any checked exception that might happens due parsing of
 * responses etc.
 *
 * @author Michael J. Simons, 2010-12-24
 */
public class OembedException extends RuntimeException {

	private static final long serialVersionUID = 7542551145054543755L;

	public OembedException(final Throwable cause) {
		super(cause.getMessage(), cause);
	}

	public OembedException(final String message) {
		super(message);
	}

}
