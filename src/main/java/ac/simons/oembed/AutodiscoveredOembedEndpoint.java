/*
 * Copyright 2015 michael-simons.eu.
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

import ac.simons.oembed.OembedResponse.Format;
import java.net.URI;

/**
 * This is an autodiscovered endpoint. It doesn't support api url generation but
 * only fixed api urls. The format is also fixed.
 *
 * @author Michael J. Simons, 2015-01-01
 */
class AutodiscoveredOembedEndpoint extends OembedEndpoint {

    /**
     * The autodiscovered api url
     */
    private final URI apiUrl;

    public AutodiscoveredOembedEndpoint(URI apiUrl, Format format) {
	this.apiUrl = apiUrl;
	super.setFormat(format);
    }

    @Override
    public URI toApiUrl(String url) {
	return this.apiUrl;
    }

    @Override
    public void setFormat(Format format) {
	// Cannot be changed
    }
}
