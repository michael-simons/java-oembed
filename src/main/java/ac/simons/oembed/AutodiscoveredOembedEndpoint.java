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

import ac.simons.oembed.OembedResponse.Format;

/**
 * This is an auto-discovered endpoint. It doesn't support api url generation but only
 * fixed api urls. The format is also fixed.
 *
 * @author Michael J. Simons, 2015-01-01
 */
final class AutodiscoveredOembedEndpoint extends OembedEndpoint {

	/**
	 * The automatically discovered api url.
	 */
	private final URI apiUrl;

	AutodiscoveredOembedEndpoint(final URI apiUrl, final Format format) {
		this.apiUrl = apiUrl;
		super.setFormat(format);
	}

	@Override
	public URI toApiUrl(final String url) {
		return this.apiUrl;
	}

	@Override
	public void setFormat(final Format format) {
		// Cannot be changed
	}

}
