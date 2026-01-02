/*
 * Created by Michael Simons, michael-simons.eu
 * and released under The BSD License
 * http://www.opensource.org/licenses/bsd-license.php
 *
 * Copyright (c) 2010-2026, Michael Simons
 * All rights reserved.
 *
 * Redistribution  and  use  in  source   and  binary  forms,  with  or   without
 * modification, are permitted provided that the following conditions are met:
 *
 * * Redistributions of source   code must retain   the above copyright   notice,
 *   this list of conditions and the following disclaimer.
 *
 * * Redistributions in binary  form must reproduce  the above copyright  notice,
 *   this list of conditions  and the following  disclaimer in the  documentation
 *   and/or other materials provided with the distribution.
 *
 * * Neither the name  of  michael-simons.eu   nor the names  of its contributors
 *   may be used  to endorse   or promote  products derived  from  this  software
 *   without specific prior written permission.
 *
 * THIS SOFTWARE IS  PROVIDED BY THE  COPYRIGHT HOLDERS AND  CONTRIBUTORS "AS IS"
 * AND ANY  EXPRESS OR  IMPLIED WARRANTIES,  INCLUDING, BUT  NOT LIMITED  TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL  THE COPYRIGHT HOLDER OR CONTRIBUTORS  BE LIABLE
 * FOR ANY  DIRECT, INDIRECT,  INCIDENTAL, SPECIAL,  EXEMPLARY, OR  CONSEQUENTIAL
 * DAMAGES (INCLUDING,  BUT NOT  LIMITED TO,  PROCUREMENT OF  SUBSTITUTE GOODS OR
 * SERVICES; LOSS  OF USE,  DATA, OR  PROFITS; OR  BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT  LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE  USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
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
