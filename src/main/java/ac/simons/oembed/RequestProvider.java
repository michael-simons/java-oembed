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

import java.net.URI;
import java.util.Optional;
import org.apache.http.client.methods.HttpGet;

/**
 * An oembed provider creates Http requests for embeddable urls.
 *
 * @author Michael J. Simons, 2014-12-28
 */
public interface RequestProvider {

    /**
     * Creates an executable http request (GET) for the given url.
     *
     * @param userAgent Our user agent
     * @param applicationName An optional application name, will be added to the
     * userAgent if present
     * @param uri The api url of the oembed endpoint
     * @return The http request for the url {@code url}
     */
    public HttpGet createRequestFor(String userAgent, Optional<String> applicationName, URI uri);
}
