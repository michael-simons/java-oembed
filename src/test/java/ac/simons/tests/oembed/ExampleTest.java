/**
 * Created by Michael Simons, michael-simons.eu
 * and released under The BSD License
 * http://www.opensource.org/licenses/bsd-license.php
 *
 * Copyright (c) 2010, Michael Simons
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
package ac.simons.tests.oembed;

import net.sf.ehcache.CacheManager;

import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.junit.Assert;
import org.junit.Test;

import ac.simons.oembed.Oembed;
import ac.simons.oembed.OembedBuilder;
import ac.simons.oembed.OembedException;
import ac.simons.oembed.OembedJsonParser;
import ac.simons.oembed.OembedProviderBuilder;
import ac.simons.oembed.OembedResponse;

/**
 * Not a real test but a simple demonstration how to use oembed
 * @author Michael J. Simons
 *
 */
public class ExampleTest {
	@Test
	public void youtubeJson() throws OembedException {		
		final Oembed oembed = new OembedBuilder(HttpClients.createDefault())
			.withProviders(
				new OembedProviderBuilder()
					.withName("youtube")
					.withFormat("json")
					.withMaxWidth(480)
					.withEndpoint("http://www.youtube.com/oembed")
					.withUrlSchemes("http://(www|de)\\.youtube\\.com/watch\\?v=.*")
					.build()
				)
			.build();
		OembedResponse response = oembed.transformUrl("http://www.youtube.com/watch?v=lh_em3-ndVw");
		System.out.println(response);
	}
	
	@Test
	public void flickrXml() throws OembedException {
		final Oembed oembed = new OembedBuilder(HttpClients.createDefault())
			.withProviders(
				new OembedProviderBuilder()
					.withName("flickr")
					.withFormat("xml")
					.withEndpoint("http://www.flickr.com/services/oembed")
					.withUrlSchemes("http://www\\.flickr\\.(com|de)/photos/.*")
					.build()
				)
			.build();
		OembedResponse response = oembed.transformUrl("http://www.flickr.com/photos/caitysparkles/5263331070/");
		System.out.println(response);
	}
	
	@Test
	public void flickrJson() throws OembedException {
		final Oembed oembed = new OembedBuilder(HttpClients.createDefault())
			.withProviders(
				new OembedProviderBuilder()
					.withName("flickr")
					.withFormat("json")
					.withEndpoint("http://www.flickr.com/services/oembed")
					.withUrlSchemes("http://www\\.flickr\\.(com|de)/photos/.*")
					.build()
				)
			.build();
		OembedResponse response = oembed.transformUrl("http://www.flickr.com/photos/pierrehanquin/6948565021");
		System.out.println(response);
	}
	
	@Test
	public void twitterJson() throws OembedException {
		final CacheManager cacheManager = CacheManager.create();
		final Oembed oembed = new OembedBuilder(HttpClients.createDefault())
			.withCacheManager(cacheManager)
			.withProviders(
				new OembedProviderBuilder()
					.withName("twitter")
					.withFormat("json")
					.withMaxWidth(480)
					.withEndpoint("https://api.twitter.com/1/statuses/oembed.%{format}")
					.withUrlSchemes("https?://twitter.com/#!/[a-z0-9_]{1,20}/status/\\d+")
					.build()
				)
			.build();
		OembedResponse response = oembed.transformUrl("http://twitter.com/#!/twitterapi/status/144840776101273600");
		System.out.println(response);
	}
	
	@Test
	public void dailyfratze() throws OembedException {
		final Oembed oembed = new Oembed(HttpClients.createDefault());
		oembed.setAutodiscovery(true);
		OembedResponse response = oembed.transformUrl("http://dailyfratze.de/michael/2010/8/22");
		System.out.println(response);
	}
	
	@Test
	public void dailyfratzeThroughCache() throws OembedException {
		final CacheManager cacheManager = CacheManager.create();		
		final Oembed oembed = new OembedBuilder(HttpClients.createMinimal(new PoolingHttpClientConnectionManager()))
			.withCacheManager(cacheManager)
			.withAutodiscovery(true)
			.withConsumer("dailyfratze.de")
			.withProviders(
				new OembedProviderBuilder()
					.withName("flickr")
					.withFormat("xml")
					.withEndpoint("http://www.flickr.com/services/oembed")
					.withUrlSchemes("http://www\\.flickr\\.(com|de)/photos/.*")
					.build()
				)
			.build();
		
		OembedResponse response = oembed.transformUrl("http://dailyfratze.de/eller82/2013/5/1");
		System.out.println(response);
		
		response = oembed.transformUrl("http://dailyfratze.de/eller82/2013/5/1");
		System.out.println(response);
				
		// 404 etc. is not called twice		
		response = oembed.transformUrl("http://www.flickr.com/photos/idontexists/123456/");
		Assert.assertNull(response);
		
		response = oembed.transformUrl("http://www.flickr.com/photos/idontexists/123456/");		
		Assert.assertNull(response);
	}
	
	@Test
	public void jsonMarshall() throws OembedException {
		OembedResponse oembedResponse = new OembedResponse();
		oembedResponse.setHtml("<div>foobar</div>");
		oembedResponse.setSource("Should be ignored");
		oembedResponse.setAuthorName("Michael Simons");		
		OembedJsonParser p = new OembedJsonParser();
		System.out.println(p.marshal(oembedResponse));
	}
}