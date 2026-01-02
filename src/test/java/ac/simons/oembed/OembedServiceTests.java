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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import ac.simons.oembed.OembedResponse.Format;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

/**
 * @author Michael J. Simons
 * @since 2015-01-02
 */
@ExtendWith(MockitoExtension.class)
public class OembedServiceTests {

	@Mock
	private HttpClient defaultHttpClient;

	@Mock
	private CacheManager cacheManager;

	private final String responseString = "{\"author_name\":\"Michael J. Simons\",\"author_url\":\"http://michael-simons.eu\",\"cache_age\":86400,\"html\":\"<iframe width='1024' height='576' src='https://biking.michael-simons.eu/tracks/1/embed?width=1024&height=576' class='bikingTrack'></iframe>\",\"provider_name\":\"biking2\",\"provider_url\":\"https://biking.michael-simons.eu\",\"title\":\"Aachen - Maastricht - Aachen\",\"type\":\"rich\",\"version\":\"1.0\"}";

	private final OembedResponse response1;

	public OembedServiceTests() throws IOException {
		this.response1 = new OembedJsonParser().unmarshal(new ByteArrayInputStream(this.responseString.getBytes()));
	}

	@Test
	public void findEndpointForShouldWorkAutodiscovery1() throws IOException {
		HttpResponse r = Mockito.mock(HttpResponse.class, Mockito.RETURNS_DEEP_STUBS);
		given(r.getStatusLine().getStatusCode()).willReturn(404);
		given(r.getEntity().getContent()).willReturn(null);
		given(r.getEntity().getContentType()).willReturn(null);

		given(this.defaultHttpClient.execute(any(HttpGet.class))).willReturn(r);

		OembedService oembedService = new OembedService(this.defaultHttpClient, null, new ArrayList<>(), null);
		oembedService.setAutodiscovery(true);

		Optional<OembedEndpoint> endpoint = oembedService.findEndpointFor("http://michael-simons.eu");
		assertThat(endpoint).isNotPresent();
		ArgumentCaptor<HttpGet> argumentCaptor = ArgumentCaptor.forClass(HttpGet.class);
		verify(this.defaultHttpClient).execute(argumentCaptor.capture());
		assertThat(argumentCaptor.getValue().getURI()).hasToString("http://michael-simons.eu");
		assertThat(oembedService.isAutodiscovery()).isTrue();
	}

	@Test
	public void findEndpointForShouldWorkAutodiscovery2() throws IOException, URISyntaxException {
		HttpResponse r = Mockito.mock(HttpResponse.class, Mockito.RETURNS_DEEP_STUBS);
		given(r.getStatusLine().getStatusCode()).willReturn(200);
		given(r.getEntity().getContentType()).willReturn(null);
		given(r.getEntity().getContent())
			.willReturn(this.getClass().getResourceAsStream("/ac/simons/oembed/autodiscovery2.html"));

		given(this.defaultHttpClient.execute(any(HttpGet.class))).willReturn(r);

		OembedService oembedService = new OembedService(this.defaultHttpClient, null, new ArrayList<>(), null);
		oembedService.setAutodiscovery(true);
		String embeddableUrl = "https://dailyfratze.de/michael/2014/10/13";

		Optional<OembedEndpoint> endpoint = oembedService.findEndpointFor(embeddableUrl);
		assertThat(endpoint).isPresent();
		ArgumentCaptor<HttpGet> argumentCaptor = ArgumentCaptor.forClass(HttpGet.class);
		verify(this.defaultHttpClient).execute(argumentCaptor.capture());
		assertThat(argumentCaptor.getValue().getURI()).hasToString(embeddableUrl);

		assertThat(endpoint.get().getFormat()).isEqualTo(Format.json);
		assertThat(endpoint.get().toApiUrl(embeddableUrl)).hasToString(
				"https://dailyfratze.de/app/oembed.json?url=https%3A%2F%2Fdailyfratze.de%2Fmichael%2F2014%2F10%2F13");
	}

	@Test
	public void findEndpointForShouldWorkAutodiscovery3() throws IOException {
		HttpResponse r = Mockito.mock(HttpResponse.class, Mockito.RETURNS_DEEP_STUBS);
		given(r.getStatusLine().getStatusCode()).willReturn(200);
		given(r.getEntity().getContentType()).willReturn(null);
		given(r.getEntity().getContent())
			.willReturn(this.getClass().getResourceAsStream("/ac/simons/oembed/autodiscovery3.html"));

		given(this.defaultHttpClient.execute(any(HttpGet.class))).willReturn(r);

		OembedService oembedService = new OembedService(this.defaultHttpClient, null, new ArrayList<>(), null);
		oembedService.setAutodiscovery(true);
		String embeddableUrl = "https://dailyfratze.de/michael/2014/10/13";

		Optional<OembedEndpoint> endpoint = oembedService.findEndpointFor(embeddableUrl);
		assertThat(endpoint).isPresent();
		ArgumentCaptor<HttpGet> argumentCaptor = ArgumentCaptor.forClass(HttpGet.class);
		verify(this.defaultHttpClient).execute(argumentCaptor.capture());
		assertThat(argumentCaptor.getValue().getURI()).hasToString(embeddableUrl);

		assertThat(endpoint.get().getFormat()).isEqualTo(Format.xml);
		assertThat(endpoint.get().toApiUrl(embeddableUrl)).hasToString(
				"https://dailyfratze.de/app/oembed.xml?url=https%3A%2F%2Fdailyfratze.de%2Fmichael%2F2014%2F10%2F13");
	}

	@Test
	public void findEndpointForShouldWorkAutodiscovery4() throws IOException {
		given(this.defaultHttpClient.execute(any(HttpGet.class))).willThrow(new IOException("foobar"));

		OembedService oembedService = new OembedService(this.defaultHttpClient, null, new ArrayList<>(), null);
		oembedService.setAutodiscovery(true);

		Optional<OembedEndpoint> endpoint = oembedService.findEndpointFor("http://michael-simons.eu");
		assertThat(endpoint).isNotPresent();
		ArgumentCaptor<HttpGet> argumentCaptor = ArgumentCaptor.forClass(HttpGet.class);
		verify(this.defaultHttpClient).execute(argumentCaptor.capture());
		assertThat(argumentCaptor.getValue().getURI()).hasToString("http://michael-simons.eu");
	}

	@Test
	public void findEndpointForShouldWorkAutodiscovery5() {
		OembedService oembedService = new OembedService(this.defaultHttpClient, null, new ArrayList<>(), null);
		oembedService.setAutodiscovery(false);

		Optional<OembedEndpoint> endpoint = oembedService.findEndpointFor("http://michael-simons.eu");
		assertThat(endpoint).isNotPresent();
		Mockito.verifyNoInteractions(this.defaultHttpClient);
	}

	@Test
	public void findEndpointForShouldWork() {
		OembedEndpoint vimeo = new OembedEndpoint();
		vimeo.setEndpoint("http://vimeo.com/api/oembed.%{format}");
		vimeo.setUrlSchemes(Arrays.asList("https?://vimeo.com/groups/.+/videos/\\d+",
				"https?://vimeo.com/channels/.+/\\d+", "https?://vimeo.com/\\d+"));

		OembedEndpoint oembedEndpoint = new OembedEndpoint();
		oembedEndpoint.setUrlSchemes(List.of("https://dailyfratze.de/\\w+/\\d{4}/\\d{2}/\\d{2}"));

		OembedService oembedService = new OembedService(this.defaultHttpClient, null,
				Arrays.asList(oembedEndpoint, vimeo), null);
		oembedService.setAutodiscovery(false);
		String embeddableUrl = "http://vimeo.com/channels/everythinganimated/111627831";

		Optional<OembedEndpoint> oendpoint = oembedService.findEndpointFor(embeddableUrl);
		assertThat(oendpoint).isPresent();
		assertThat(oendpoint.get().toApiUrl(embeddableUrl)).hasToString(
				"http://vimeo.com/api/oembed.json?url=http%3A%2F%2Fvimeo.com%2Fchannels%2Feverythinganimated%2F111627831");
		Mockito.verifyNoInteractions(this.defaultHttpClient);
	}

	@Test
	public void executeRequestShouldWork1() throws IOException {
		HttpGet request = new HttpGet("http://michael-simons.eu");
		given(this.defaultHttpClient.execute(request)).willThrow(new IOException("foobar"));

		OembedService oembedService = new OembedService(this.defaultHttpClient, null, new ArrayList<>(), null);

		assertThat(oembedService.executeRequest(request)).isNull();
		verify(this.defaultHttpClient).execute(request);

	}

	@Test
	public void executeRequestShouldWork2() throws IOException {
		HttpGet request = new HttpGet("http://michael-simons.eu");

		HttpResponse r = Mockito.mock(HttpResponse.class, Mockito.RETURNS_DEEP_STUBS);
		given(r.getStatusLine().getStatusCode()).willReturn(404);
		given(r.getEntity().getContent()).willReturn(null);
		given(r.getEntity().getContentType()).willReturn(null);

		given(this.defaultHttpClient.execute(request)).willReturn(r);

		OembedService oembedService = new OembedService(this.defaultHttpClient, null, new ArrayList<>(), null);

		assertThat(oembedService.executeRequest(request)).isNull();
		verify(this.defaultHttpClient).execute(request);
	}

	@Test
	public void executeRequestShouldWork3() throws IOException {
		HttpGet request = new HttpGet("http://michael-simons.eu");

		HttpResponse r = Mockito.mock(HttpResponse.class, Mockito.RETURNS_DEEP_STUBS);
		given(r.getStatusLine().getStatusCode()).willReturn(200);
		given(r.getEntity().getContentType()).willReturn(null);
		given(r.getEntity().getContent()).willReturn(new ByteArrayInputStream("Hallo, Welt".getBytes()));

		given(this.defaultHttpClient.execute(request)).willReturn(r);

		OembedService oembedService = new OembedService(this.defaultHttpClient, null, new ArrayList<>(), null);

		assertThat(oembedService.executeRequest(request)).isNotNull();
		verify(this.defaultHttpClient).execute(request);
	}

	@Test
	public void setCacheNameShouldWork() {
		given(this.cacheManager.cacheExists("ac.simons.oembed.OembedService")).willReturn(false);
		given(this.cacheManager.cacheExists("x")).willReturn(true);

		OembedService oembedService;
		oembedService = new OembedService(this.defaultHttpClient, null, new ArrayList<>(), null);
		oembedService.setCacheName("x");
		assertThat(oembedService.getCacheName()).isEqualTo("x");

		oembedService = new OembedService(this.defaultHttpClient, this.cacheManager, new ArrayList<>(), null);
		oembedService.setCacheName("x");
		assertThat(oembedService.getCacheName()).isEqualTo("x");

		oembedService.setCacheName("y");
		assertThat(oembedService.getCacheName()).isEqualTo("y");

		verify(this.cacheManager).cacheExists(OembedService.class.getName());
		verify(this.cacheManager).cacheExists("x");
		verify(this.cacheManager).removeCache("x");
		Mockito.verifyNoMoreInteractions(this.cacheManager);
	}

	@Test
	public void getOembedResponseForShouldWork1() {
		OembedService oembedService = new OembedService(this.defaultHttpClient, null, new ArrayList<>(), null);
		assertThat(oembedService.getOembedResponseFor(null)).isNotPresent();
		assertThat(oembedService.getOembedResponseFor("	    ")).isNotPresent();
		assertThat(oembedService.getOembedResponseFor("https://dailyfratze.de/michael/2014/10/13")).isNotPresent();
	}

	/**
	 * Response from cache
	 */
	@Test
	public void getOembedResponseForShouldWork2() {
		Ehcache cache = Mockito.mock(Ehcache.class);
		String embeddableUrl = "https://biking.michael-simons.eu/tracks/1";
		given(cache.get(embeddableUrl)).willReturn(new Element(embeddableUrl, this.response1));
		given(this.cacheManager.addCacheIfAbsent("testCache")).willReturn(cache);

		OembedService oembedService = new OembedService(this.defaultHttpClient, this.cacheManager, new ArrayList<>(),
				null);
		oembedService.setCacheName("testCache");
		assertThat(oembedService.getOembedResponseFor(null)).isNotPresent();
		assertThat(oembedService.getOembedResponseFor("	    ")).isNotPresent();
		Optional<OembedResponse> oembedResponse = oembedService.getOembedResponseFor(embeddableUrl);
		assertThat(oembedResponse).isPresent();
		OembedResponse response = oembedResponse.get();
		assertThat(response.getAuthorName()).isEqualTo("Michael J. Simons");
		assertThat(response.getAuthorUrl()).isEqualTo("http://michael-simons.eu");
		assertThat(response.getCacheAge()).isEqualTo(Long.valueOf(86400L));
		assertThat(response.getHtml()).isEqualTo(
				"<iframe width='1024' height='576' src='https://biking.michael-simons.eu/tracks/1/embed?width=1024&height=576' class='bikingTrack'></iframe>");
		assertThat(response.getProviderName()).isEqualTo("biking2");
		assertThat(response.getProviderUrl()).isEqualTo("https://biking.michael-simons.eu");
		assertThat(response.getTitle()).isEqualTo("Aachen - Maastricht - Aachen");
		assertThat(response.getType()).isEqualTo("rich");
		assertThat(response.getVersion()).isEqualTo("1.0");
		assertThat(oembedService.getCacheName()).isEqualTo("testCache");

		verify(this.cacheManager).addCacheIfAbsent("testCache");
		verify(this.cacheManager).cacheExists(OembedService.class.getName());
		verify(cache).get(embeddableUrl);
		Mockito.verifyNoMoreInteractions(cache, this.cacheManager);
		Mockito.verifyNoInteractions(this.defaultHttpClient);
	}

	/**
	 * Handle invalid content gracefully and also add to cache.
	 * @throws IOException all of them
	 */
	@Test
	public void getOembedResponseForShouldWork3() throws IOException {
		String embeddableUrl = "https://biking.michael-simons.eu/tracks/1";

		OembedEndpoint oembedEndpoint = new OembedEndpoint();
		oembedEndpoint.setName("biking");
		oembedEndpoint.setEndpoint("https://biking.michael-simons.eu/oembed");
		oembedEndpoint.setMaxWidth(480);
		oembedEndpoint.setMaxHeight(360);
		oembedEndpoint.setUrlSchemes(List.of("https://biking\\.michael-simons\\.eu/tracks/.*"));

		HttpResponse r = Mockito.mock(HttpResponse.class, Mockito.RETURNS_DEEP_STUBS);
		given(r.getStatusLine().getStatusCode()).willReturn(200);
		given(r.getEntity().getContentType()).willReturn(null);
		given(r.getEntity().getContent()).willReturn(new ByteArrayInputStream("Hallo, Welt".getBytes()));

		given(this.defaultHttpClient.execute(any(HttpGet.class))).willReturn(r);

		Ehcache cache = Mockito.mock(Ehcache.class);
		given(cache.get(embeddableUrl)).willReturn(null);
		given(this.cacheManager.addCacheIfAbsent("testCache")).willReturn(cache);

		OembedService oembedService = new OembedService(this.defaultHttpClient, this.cacheManager,
				List.of(oembedEndpoint), null);
		oembedService.setCacheName("testCache");
		assertThat(oembedService.getOembedResponseFor(embeddableUrl)).isNotPresent();
		ArgumentCaptor<HttpGet> argumentCaptor = ArgumentCaptor.forClass(HttpGet.class);
		verify(this.defaultHttpClient).execute(argumentCaptor.capture());
		assertThat(argumentCaptor.getValue().getURI()).hasToString(
				"https://biking.michael-simons.eu/oembed?format=json&url=https%3A%2F%2Fbiking.michael-simons.eu%2Ftracks%2F1&maxwidth=480&maxheight=360");

		verify(this.cacheManager, times(2)).addCacheIfAbsent("testCache");
		verify(this.cacheManager).cacheExists(OembedService.class.getName());
		verify(cache).get(embeddableUrl);
		verify(cache).put(any(Element.class));

		verifyNoMoreInteractions(cache, this.cacheManager, this.defaultHttpClient);
	}

	/**
	 * Embedding through configured endpoint including request provider.
	 * @throws IOException all of them
	 */
	@Test
	public void getOembedResponseForShouldWork4() throws IOException {
		String embeddableUrl = "https://biking.michael-simons.eu/tracks/1";

		OembedEndpoint oembedEndpoint = new OembedEndpoint();
		oembedEndpoint.setName("biking");
		oembedEndpoint.setEndpoint("https://biking.michael-simons.eu/oembed");
		oembedEndpoint.setMaxWidth(480);
		oembedEndpoint.setMaxHeight(360);
		oembedEndpoint.setUrlSchemes(List.of("https://biking\\.michael-simons\\.eu/tracks/.*"));

		HttpResponse r = Mockito.mock(HttpResponse.class, Mockito.RETURNS_DEEP_STUBS);
		given(r.getStatusLine().getStatusCode()).willReturn(200);
		given(r.getEntity().getContentType()).willReturn(null);
		given(r.getEntity().getContent()).willReturn(new ByteArrayInputStream(this.responseString.getBytes()));

		given(this.defaultHttpClient.execute(any(HttpGet.class))).willReturn(r);

		Ehcache cache = Mockito.mock(Ehcache.class);
		given(cache.get(embeddableUrl)).willReturn(null);
		given(this.cacheManager.addCacheIfAbsent("testCache")).willReturn(cache);

		OembedService oembedService = new OembedService(this.defaultHttpClient, this.cacheManager,
				List.of(oembedEndpoint), null);
		oembedService.setCacheName("testCache");
		assertThat(oembedService.getOembedResponseFor(embeddableUrl)).isPresent();
		ArgumentCaptor<HttpGet> argumentCaptor = ArgumentCaptor.forClass(HttpGet.class);
		verify(this.defaultHttpClient).execute(argumentCaptor.capture());
		assertThat(argumentCaptor.getValue().getURI()).hasToString(
				"https://biking.michael-simons.eu/oembed?format=json&url=https%3A%2F%2Fbiking.michael-simons.eu%2Ftracks%2F1&maxwidth=480&maxheight=360");

		verify(this.cacheManager, times(2)).addCacheIfAbsent("testCache");
		verify(this.cacheManager).cacheExists(OembedService.class.getName());
		verify(cache).get(embeddableUrl);
		verify(cache).put(any(Element.class));

		verifyNoMoreInteractions(cache, this.cacheManager, this.defaultHttpClient);
	}

	/**
	 * Embedding through auto discovered endpoint using default request provider.
	 * @throws IOException all of them
	 */
	@Test
	public void getOembedResponseForShouldWork5() throws IOException {
		String embeddableUrl = "https://dailyfratze.de/michael/2014/10/13";
		HttpResponse r1 = Mockito.mock(HttpResponse.class, Mockito.RETURNS_DEEP_STUBS);
		given(r1.getStatusLine().getStatusCode()).willReturn(200);
		given(r1.getEntity().getContentType()).willReturn(null);
		given(r1.getEntity().getContent())
			.willReturn(this.getClass().getResourceAsStream("/ac/simons/oembed/autodiscovery2.html"));

		HttpResponse r2 = Mockito.mock(HttpResponse.class, Mockito.RETURNS_DEEP_STUBS);
		given(r2.getStatusLine().getStatusCode()).willReturn(200);
		given(r2.getEntity().getContentType()).willReturn(null);
		given(r2.getEntity().getContent()).willReturn(new ByteArrayInputStream(this.responseString.getBytes()));

		given(this.defaultHttpClient.execute(any(HttpGet.class))).willAnswer((Answer<?>) invocation -> {
			final String url = invocation.<HttpGet>getArgument(0).getURI().toString();
			HttpResponse rv = null;
			if (embeddableUrl.equals(url)) {
				rv = r1;
			}
			else if ("https://dailyfratze.de/app/oembed.json?url=https%3A%2F%2Fdailyfratze.de%2Fmichael%2F2014%2F10%2F13"
				.equals(url)) {
				rv = r2;
			}
			return rv;
		});

		OembedService oembedService = new OembedService(this.defaultHttpClient, null, new ArrayList<>(), null);
		oembedService.setAutodiscovery(true);

		assertThat(oembedService.getOembedResponseFor(embeddableUrl)).isPresent();
		verify(this.defaultHttpClient, times(2)).execute(any(HttpGet.class));
		verifyNoMoreInteractions(this.cacheManager, this.defaultHttpClient);
	}

	@Test
	public void embedUrlsShouldWork1() {
		OembedService oembedService = new OembedService(this.defaultHttpClient, null, new ArrayList<>(), null);
		assertThat(oembedService.embedUrls(null, null)).isNull();
		assertThat(oembedService.embedUrls("", null)).isEmpty();
		assertThat(oembedService.embedUrls("	", null)).isEqualTo("	");
		assertThat(oembedService.embedUrls(" ", null)).isEqualTo(" ");
	}

	/**
	 * Get also the "orElse" branch in selecting the renderer as no endpoint is
	 * configured, also no renders.
	 */
	@Test
	public void embedUrlsShouldWork2() {
		Ehcache cache = Mockito.mock(Ehcache.class);
		String embeddableUrl = "https://biking.michael-simons.eu/tracks/1";
		given(cache.get(embeddableUrl)).willReturn(new Element(embeddableUrl, this.response1));
		given(this.cacheManager.addCacheIfAbsent("testCache")).willReturn(cache);

		OembedService oembedService = new OembedService(this.defaultHttpClient, this.cacheManager, new ArrayList<>(),
				null);
		oembedService.setCacheName("testCache");

		String in = "<p>Vor langer Zeit fuhr ich diesen Weg: <a href=\"https://biking.michael-simons.eu/tracks/1\">von Aachen nach Maastricht und zurück</a>.</p>";
		String expected = "<p>Vor langer Zeit fuhr ich diesen Weg: <iframe width=\"1024\" height=\"576\" src=\"https://biking.michael-simons.eu/tracks/1/embed?width=1024&height=576\" class=\"bikingTrack\"></iframe>.</p>";

		assertThat(oembedService.embedUrls(in, null)).isEqualTo(expected);
	}

	@Test
	public void embedUrlsShouldWork3() {
		Ehcache cache = Mockito.mock(Ehcache.class);
		String embeddableUrl = "https://biking.michael-simons.eu/tracks/1";
		given(cache.get(embeddableUrl)).willReturn(new Element(embeddableUrl, this.response1));
		given(this.cacheManager.addCacheIfAbsent("testCache")).willReturn(cache);

		OembedEndpoint oembedEndpoint = new OembedEndpoint();
		oembedEndpoint.setName("biking");
		oembedEndpoint.setEndpoint("https://biking.michael-simons.eu/oembed");
		oembedEndpoint.setMaxWidth(480);
		oembedEndpoint.setMaxHeight(360);
		oembedEndpoint.setUrlSchemes(List.of("https://biking\\.michael-simons\\.eu/tracks/.*"));
		oembedEndpoint.setResponseRendererClass(BrokenRenderer.class);

		OembedService oembedService = new OembedService(this.defaultHttpClient, this.cacheManager,
				List.of(oembedEndpoint), null);
		oembedService.setCacheName("testCache");

		String in = "<p>Vor langer Zeit fuhr ich diesen Weg: <a href=\"https://biking.michael-simons.eu/tracks/1\">von Aachen nach Maastricht und zurück</a>. Hier der Bericht: <a href=\"http://test.com\">Bericht</a>.</p>";

		assertThat(oembedService.embedUrls(in, null)).isEqualTo(in);
	}

	@Test
	public void embedUrlsShouldWork4() {

		OembedService oembedService = new OembedService(this.defaultHttpClient, null, new ArrayList<>(), null);
		assertThatExceptionOfType(OembedException.class)
			.isThrownBy(() -> oembedService.embedUrls(null, null, Integer.class))
			.withMessage("Invalid target class: java.lang.Integer");
	}

	@Test
	public void misc() {
		OembedService oembedService = new OembedService(this.defaultHttpClient, this.cacheManager, new ArrayList<>(),
				null);
		assertThat(oembedService.getDefaultCacheAge()).isEqualTo(3600);
		oembedService.setDefaultCacheAge(10);
		assertThat(oembedService.getDefaultCacheAge()).isEqualTo(10);
	}

}
