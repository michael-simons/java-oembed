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

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Michael J. Simons, 2014-12-28
 */
public class OembedResponseTests {

	@Test
	public void beanShouldWorkAsExpected() {
		final OembedResponse oembedResponse = new OembedResponse();
		assertThat(oembedResponse.getType()).isNull();
		assertThat(oembedResponse.getVersion()).isNull();
		assertThat(oembedResponse.getTitle()).isNull();
		assertThat(oembedResponse.getAuthorName()).isNull();
		assertThat(oembedResponse.getAuthorUrl()).isNull();
		assertThat(oembedResponse.getProviderName()).isNull();
		assertThat(oembedResponse.getProviderUrl()).isNull();
		assertThat(oembedResponse.getCacheAge()).isNull();
		assertThat(oembedResponse.getThumbnailUrl()).isNull();
		assertThat(oembedResponse.getThumbnailWidth()).isNull();
		assertThat(oembedResponse.getThumbnailHeight()).isNull();
		assertThat(oembedResponse.getUrl()).isNull();
		assertThat(oembedResponse.getHtml()).isNull();
		assertThat(oembedResponse.getWidth()).isNull();
		assertThat(oembedResponse.getHeight()).isNull();

		oembedResponse.setType("type");
		oembedResponse.setVersion("version");
		oembedResponse.setTitle("title");
		oembedResponse.setAuthorName("authorName");
		oembedResponse.setAuthorUrl("authorUrl");
		oembedResponse.setProviderName("providerName");
		oembedResponse.setProviderUrl("providerUrl");
		oembedResponse.setCacheAge(4711L);
		oembedResponse.setThumbnailUrl("thumbnailUrl");
		oembedResponse.setThumbnailWidth(23);
		oembedResponse.setThumbnailHeight(42);
		oembedResponse.setUrl("url");
		oembedResponse.setHtml("html");
		oembedResponse.setWidth(44);
		oembedResponse.setHeight(55);

		assertThat(oembedResponse.getType()).isEqualTo("type");
		assertThat(oembedResponse.getVersion()).isEqualTo("version");
		assertThat(oembedResponse.getTitle()).isEqualTo("title");
		assertThat(oembedResponse.getAuthorName()).isEqualTo("authorName");
		assertThat(oembedResponse.getAuthorUrl()).isEqualTo("authorUrl");
		assertThat(oembedResponse.getProviderName()).isEqualTo("providerName");
		assertThat(oembedResponse.getProviderUrl()).isEqualTo("providerUrl");
		assertThat(oembedResponse.getCacheAge()).isEqualTo(Long.valueOf(4711L));
		assertThat(oembedResponse.getThumbnailUrl()).isEqualTo("thumbnailUrl");
		assertThat(oembedResponse.getThumbnailWidth()).isEqualTo(Integer.valueOf(23));
		assertThat(oembedResponse.getThumbnailHeight()).isEqualTo(Integer.valueOf(42));
		assertThat(oembedResponse.getUrl()).isEqualTo("url");
		assertThat(oembedResponse.getHtml()).isEqualTo("html");
		assertThat(oembedResponse.getWidth()).isEqualTo(Integer.valueOf(44));
		assertThat(oembedResponse.getHeight()).isEqualTo(Integer.valueOf(55));
	}

}
