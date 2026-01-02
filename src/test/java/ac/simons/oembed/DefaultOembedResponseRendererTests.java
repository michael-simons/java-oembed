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
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Michael J. Simons, 2015-07-02
 */
public class DefaultOembedResponseRendererTests {

	@Test
	public void renderShouldWorkForPhotos() {
		final DefaultOembedResponseRenderer renderer = new DefaultOembedResponseRenderer();
		OembedResponse response;
		response = new OembedResponse();
		response.setType("photo");
		response.setUrl("http://info.michael-simons.eu/wp-content/uploads/2015/05/Blogcake.jpeg");
		response.setWidth(200);
		response.setHeight(100);

		assertThat(renderer.render(response, null)).isEqualTo(
				"<img src=\"http://info.michael-simons.eu/wp-content/uploads/2015/05/Blogcake.jpeg\" style=\"width:200px; height:100px;\" alt=\"\" title=\"\"/>");
		response.setTitle("test");
		assertThat(renderer.render(response, null)).isEqualTo(
				"<img src=\"http://info.michael-simons.eu/wp-content/uploads/2015/05/Blogcake.jpeg\" style=\"width:200px; height:100px;\" alt=\"test\" title=\"test\"/>");
	}

	@Test
	public void renderShouldWorkForVideos() {
		final DefaultOembedResponseRenderer renderer = new DefaultOembedResponseRenderer();
		OembedResponse response;
		response = new OembedResponse();
		response.setType("video");
		response.setHtml("<html />");
		assertThat(renderer.render(response, null)).isEqualTo("<html />");
	}

	@Test
	public void renderShouldWorkForLink() {
		final DefaultOembedResponseRenderer renderer = new DefaultOembedResponseRenderer();
		final Element originalAnchor = Mockito.mock(Element.class);
		Mockito.when(originalAnchor.absUrl("href")).thenReturn("http://info.michael-simons.eu");
		OembedResponse response;
		response = new OembedResponse();
		response.setType("link");
		assertThat(renderer.render(response, originalAnchor))
			.isEqualTo("<a href=\"http://info.michael-simons.eu\">http://info.michael-simons.eu</a>");
		response.setTitle("title");
		response.setUrl("http://info.michael-simons.eu/2015/05/24/java20/");
		assertThat(renderer.render(response, originalAnchor))
			.isEqualTo("<a href=\"http://info.michael-simons.eu/2015/05/24/java20/\">title</a>");
	}

	@Test
	public void renderShouldWorkForRich() {
		final DefaultOembedResponseRenderer renderer = new DefaultOembedResponseRenderer();
		OembedResponse response;
		response = new OembedResponse();
		response.setType("rich");
		response.setHtml("<html />");
		assertThat(renderer.render(response, null)).isEqualTo("<html />");
	}

}
