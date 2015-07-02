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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.apache.http.impl.client.DefaultHttpClient;

/**
 *
 * @author Michael J. Simons, 2015-07-02
 */
public class Test {

    public static void main(String... a) {
	final List<OembedEndpoint> endpoints = new ArrayList<>();
	OembedEndpoint endpoint;

	endpoint = new OembedEndpoint();
	endpoint.setName("youtube");
	endpoint.setFormat(Format.json);
	endpoint.setMaxWidth(480);
	endpoint.setEndpoint("https://www.youtube.com/oembed");
	endpoint.setUrlSchemes(Arrays.asList("https?://(www|de)\\.youtube\\.com/watch\\?v=.*"));
	// Optional, specialised renderer, not included here
	// endpoint.setResponseRendererClass(YoutubeRenderer.class);
	endpoints.add(endpoint);
		
	final OembedService oembedService = new OembedService(new DefaultHttpClient(), null, endpoints, "some-app");
	System.out.println(oembedService.embedUrls("Need some action... <a href=\"https://www.youtube.com/watch?v=dgL6ovr3DJM\">The Hoff!</a>", Optional.empty()));
    }
}
