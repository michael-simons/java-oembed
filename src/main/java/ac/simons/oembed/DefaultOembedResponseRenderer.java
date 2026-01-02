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

import org.jsoup.nodes.Element;

/**
 * A simple, default oembed response renderer. Uses the delivered HTML in most cases.
 *
 * @author Michael J. Simons
 * @since 2015-01-02
 */
class DefaultOembedResponseRenderer implements OembedResponseRenderer {

	@Override
	public String render(final OembedResponse response, final Element originalAnchor) {
		String rv = null;
		if (response.getType().equalsIgnoreCase("photo")) {
			final String title = (response.getTitle() != null) ? response.getTitle() : "";
			rv = String.format("<img src=\"%s\" style=\"width:%dpx; height:%dpx;\" alt=\"%s\" title=\"%s\"/>",
					response.getUrl(), response.getWidth(), response.getHeight(), title, title);
		}
		else if (response.getType().equalsIgnoreCase("video")) {
			rv = response.getHtml();
		}
		else if (response.getType().equalsIgnoreCase("link")) {
			final String originalUrl = originalAnchor.absUrl("href");
			final String title = (response.getTitle() != null) ? response.getTitle() : originalUrl;
			final String url = (response.getUrl() != null) ? response.getUrl() : originalUrl;
			rv = String.format("<a href=\"%s\">%s</a>", url, title);
		}
		else if (response.getType().equalsIgnoreCase("rich")) {
			rv = response.getHtml();
		}
		return rv;
	}

}
