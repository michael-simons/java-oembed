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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;

import ac.simons.oembed.OembedResponse.Format;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;

/**
 *
 * @author Michael J. Simons
 * @since 2015-01-02
 */
@ExtendWith(MockitoExtension.class)
public class OembedServiceTest {

    @Mock
    private HttpClient defaultHttpClient;

    @Mock
    private CacheManager cacheManager;

    private final String responseString = "{\"author_name\":\"Michael J. Simons\",\"author_url\":\"http://michael-simons.eu\",\"cache_age\":86400,\"html\":\"<iframe width='1024' height='576' src='https://biking.michael-simons.eu/tracks/1/embed?width=1024&height=576' class='bikingTrack'></iframe>\",\"provider_name\":\"biking2\",\"provider_url\":\"https://biking.michael-simons.eu\",\"title\":\"Aachen - Maastricht - Aachen\",\"type\":\"rich\",\"version\":\"1.0\"}";
    private final OembedResponse response1;

    public OembedServiceTest() throws IOException {
	response1 = new OembedJsonParser().unmarshal(new ByteArrayInputStream(responseString.getBytes()));
    }

    @Test
    public void findEndpointForShouldWorkAutodiscovery1() throws IOException {
	HttpResponse r = Mockito.mock(HttpResponse.class, Mockito.RETURNS_DEEP_STUBS);
	when(r.getStatusLine().getStatusCode()).thenReturn(404);
	when(r.getEntity().getContent()).thenReturn(null);
        when(r.getEntity().getContentType()).thenReturn(null);

	when(defaultHttpClient.execute(any(HttpGet.class))).thenReturn(r);

	OembedService oembedService = new OembedService(defaultHttpClient, null, new ArrayList<>(), null);
	oembedService.setAutodiscovery(true);

	Optional<OembedEndpoint> endpoint = oembedService.findEndpointFor("http://michael-simons.eu");
	Assertions.assertFalse(endpoint.isPresent());
	ArgumentCaptor<HttpGet> argumentCaptor = ArgumentCaptor.forClass(HttpGet.class);
	verify(defaultHttpClient).execute(argumentCaptor.capture());
	Assertions.assertEquals("http://michael-simons.eu", argumentCaptor.getValue().getURI().toString());
	Assertions.assertTrue(oembedService.isAutodiscovery());
    }

    @Test
    public void findEndpointForShouldWorkAutodiscovery2() throws IOException, URISyntaxException {
	HttpResponse r = Mockito.mock(HttpResponse.class, Mockito.RETURNS_DEEP_STUBS);
	when(r.getStatusLine().getStatusCode()).thenReturn(200);
	when(r.getEntity().getContentType()).thenReturn(null);
	when(r.getEntity().getContent()).thenReturn(this.getClass().getResourceAsStream("/ac/simons/oembed/autodiscovery2.html"));

	when(defaultHttpClient.execute(any(HttpGet.class))).thenReturn(r);

	OembedService oembedService = new OembedService(defaultHttpClient, null, new ArrayList<>(), null);
	oembedService.setAutodiscovery(true);
	String embeddableUrl = "https://dailyfratze.de/michael/2014/10/13";

	Optional<OembedEndpoint> endpoint = oembedService.findEndpointFor(embeddableUrl);
	Assertions.assertTrue(endpoint.isPresent());
	ArgumentCaptor<HttpGet> argumentCaptor = ArgumentCaptor.forClass(HttpGet.class);
	verify(defaultHttpClient).execute(argumentCaptor.capture());
	Assertions.assertEquals(embeddableUrl, argumentCaptor.getValue().getURI().toString());

	Assertions.assertEquals(Format.json, endpoint.get().getFormat());
	Assertions.assertEquals("https://dailyfratze.de/app/oembed.json?url=https%3A%2F%2Fdailyfratze.de%2Fmichael%2F2014%2F10%2F13", endpoint.get().toApiUrl(embeddableUrl).toString());
    }

    @Test
    public void findEndpointForShouldWorkAutodiscovery3() throws IOException {
	HttpResponse r = Mockito.mock(HttpResponse.class, Mockito.RETURNS_DEEP_STUBS);
	when(r.getStatusLine().getStatusCode()).thenReturn(200);
	when(r.getEntity().getContentType()).thenReturn(null);
	when(r.getEntity().getContent()).thenReturn(this.getClass().getResourceAsStream("/ac/simons/oembed/autodiscovery3.html"));

	when(defaultHttpClient.execute(any(HttpGet.class))).thenReturn(r);

	OembedService oembedService = new OembedService(defaultHttpClient, null, new ArrayList<>(), null);
	oembedService.setAutodiscovery(true);
	String embeddableUrl = "https://dailyfratze.de/michael/2014/10/13";

	Optional<OembedEndpoint> endpoint = oembedService.findEndpointFor(embeddableUrl);
	Assertions.assertTrue(endpoint.isPresent());
	ArgumentCaptor<HttpGet> argumentCaptor = ArgumentCaptor.forClass(HttpGet.class);
	verify(defaultHttpClient).execute(argumentCaptor.capture());
	Assertions.assertEquals(embeddableUrl, argumentCaptor.getValue().getURI().toString());

	Assertions.assertEquals(Format.xml, endpoint.get().getFormat());
	Assertions.assertEquals("https://dailyfratze.de/app/oembed.xml?url=https%3A%2F%2Fdailyfratze.de%2Fmichael%2F2014%2F10%2F13", endpoint.get().toApiUrl(embeddableUrl).toString());
    }

    @Test
    public void findEndpointForShouldWorkAutodiscovery4() throws IOException {
	when(defaultHttpClient.execute(any(HttpGet.class))).thenThrow(new IOException("foobar"));

	OembedService oembedService = new OembedService(defaultHttpClient, null, new ArrayList<>(), null);
	oembedService.setAutodiscovery(true);

	Optional<OembedEndpoint> endpoint = oembedService.findEndpointFor("http://michael-simons.eu");
	Assertions.assertFalse(endpoint.isPresent());
	ArgumentCaptor<HttpGet> argumentCaptor = ArgumentCaptor.forClass(HttpGet.class);
	verify(defaultHttpClient).execute(argumentCaptor.capture());
	Assertions.assertEquals("http://michael-simons.eu", argumentCaptor.getValue().getURI().toString());
    }

    @Test
    public void findEndpointForShouldWorkAutodiscovery5() throws IOException {
	OembedService oembedService = new OembedService(defaultHttpClient, null, new ArrayList<>(), null);
	oembedService.setAutodiscovery(false);

	Optional<OembedEndpoint> endpoint = oembedService.findEndpointFor("http://michael-simons.eu");
	Assertions.assertFalse(endpoint.isPresent());
	Mockito.verifyNoInteractions(defaultHttpClient);
    }

    @Test
    public void findEndpointForShouldWork() throws IOException {
	OembedEndpoint vimeo = new OembedEndpoint();
	vimeo.setEndpoint("http://vimeo.com/api/oembed.%{format}");
	vimeo.setUrlSchemes(Arrays.asList("https?://vimeo.com/groups/.+/videos/\\d+", "https?://vimeo.com/channels/.+/\\d+", "https?://vimeo.com/\\d+"));

	OembedEndpoint oembedEndpoint = new OembedEndpoint();
	oembedEndpoint.setUrlSchemes(Arrays.asList("https://dailyfratze.de/\\w+/\\d{4}/\\d{2}/\\d{2}"));

	OembedService oembedService = new OembedService(defaultHttpClient, null, Arrays.asList(oembedEndpoint, vimeo), null);
	oembedService.setAutodiscovery(false);
	String embeddableUrl = "http://vimeo.com/channels/everythinganimated/111627831";

	Optional<OembedEndpoint> oendpoint = oembedService.findEndpointFor(embeddableUrl);
	Assertions.assertTrue(oendpoint.isPresent());
	Assertions.assertEquals("http://vimeo.com/api/oembed.json?url=http%3A%2F%2Fvimeo.com%2Fchannels%2Feverythinganimated%2F111627831", oendpoint.get().toApiUrl(embeddableUrl).toString());
	Mockito.verifyNoInteractions(defaultHttpClient);
    }

    @Test
    public void executeRequestShouldWork1() throws IOException {
	HttpGet request = new HttpGet("http://michael-simons.eu");
	when(defaultHttpClient.execute(request)).thenThrow(new IOException("foobar"));

	OembedService oembedService = new OembedService(defaultHttpClient, null, new ArrayList<>(), null);


	    Assertions.assertNull(oembedService.executeRequest(request));
	    verify(defaultHttpClient).execute(request);

    }

    @Test
    public void executeRequestShouldWork2() throws IOException {
	HttpGet request = new HttpGet("http://michael-simons.eu");

	HttpResponse r = Mockito.mock(HttpResponse.class, Mockito.RETURNS_DEEP_STUBS);
	when(r.getStatusLine().getStatusCode()).thenReturn(404);
	when(r.getEntity().getContent()).thenReturn(null);
        when(r.getEntity().getContentType()).thenReturn(null);

	when(defaultHttpClient.execute(request)).thenReturn(r);

	OembedService oembedService = new OembedService(defaultHttpClient, null, new ArrayList<>(), null);

	Assertions.assertNull(oembedService.executeRequest(request));
	verify(defaultHttpClient).execute(request);
    }

    @Test
    public void executeRequestShouldWork3() throws IOException {
	HttpGet request = new HttpGet("http://michael-simons.eu");

	HttpResponse r = Mockito.mock(HttpResponse.class, Mockito.RETURNS_DEEP_STUBS);
	when(r.getStatusLine().getStatusCode()).thenReturn(200);
	when(r.getEntity().getContentType()).thenReturn(null);
	when(r.getEntity().getContent()).thenReturn(new ByteArrayInputStream("Hallo, Welt".getBytes()));

	when(defaultHttpClient.execute(request)).thenReturn(r);

	OembedService oembedService = new OembedService(defaultHttpClient, null, new ArrayList<>(), null);

	Assertions.assertNotNull(oembedService.executeRequest(request));
	verify(defaultHttpClient).execute(request);
    }

    @Test
    public void setCacheNameShouldWork() {
	    when(cacheManager.cacheExists("ac.simons.oembed.OembedService")).thenReturn(false);
	    when(cacheManager.cacheExists("x")).thenReturn(true);

	OembedService oembedService;
	oembedService = new OembedService(defaultHttpClient, null, new ArrayList<>(), null);
	oembedService.setCacheName("x");
	Assertions.assertEquals("x", oembedService.getCacheName());

	oembedService = new OembedService(defaultHttpClient, cacheManager, new ArrayList<>(), null);
	oembedService.setCacheName("x");
	Assertions.assertEquals("x", oembedService.getCacheName());

	oembedService.setCacheName("y");
	Assertions.assertEquals("y", oembedService.getCacheName());

	verify(cacheManager).cacheExists(OembedService.class.getName());
	verify(cacheManager).cacheExists("x");
	verify(cacheManager).removeCache("x");
	Mockito.verifyNoMoreInteractions(cacheManager);
    }

    @Test
    public void getOembedResponseForShouldWork1() {
	OembedService oembedService = new OembedService(defaultHttpClient, null, new ArrayList<>(), null);
	Assertions.assertFalse(oembedService.getOembedResponseFor(null).isPresent());
	Assertions.assertFalse(oembedService.getOembedResponseFor("	    ").isPresent());
	Assertions.assertFalse(oembedService.getOembedResponseFor("https://dailyfratze.de/michael/2014/10/13").isPresent());
    }

    /**
     * Response from cache
     */
    @Test
    public void getOembedResponseForShouldWork2() {
	Ehcache cache = Mockito.mock(Ehcache.class);
	String embeddableUrl = "https://biking.michael-simons.eu/tracks/1";
	when(cache.get(embeddableUrl)).thenReturn(new Element(embeddableUrl, response1));
	when(cacheManager.addCacheIfAbsent("testCache")).thenReturn(cache);

	OembedService oembedService = new OembedService(defaultHttpClient, cacheManager, new ArrayList<>(), null);
	oembedService.setCacheName("testCache");
	Assertions.assertFalse(oembedService.getOembedResponseFor(null).isPresent());
	Assertions.assertFalse(oembedService.getOembedResponseFor("	    ").isPresent());
	Optional<OembedResponse> oembedResponse = oembedService.getOembedResponseFor(embeddableUrl);
	Assertions.assertTrue(oembedResponse.isPresent());
	OembedResponse response = oembedResponse.get();
	Assertions.assertEquals("Michael J. Simons", response.getAuthorName());
	Assertions.assertEquals("http://michael-simons.eu", response.getAuthorUrl());
	Assertions.assertEquals(Long.valueOf(86400l), response.getCacheAge());
	Assertions.assertEquals("<iframe width='1024' height='576' src='https://biking.michael-simons.eu/tracks/1/embed?width=1024&height=576' class='bikingTrack'></iframe>", response.getHtml());
	Assertions.assertEquals("biking2", response.getProviderName());
	Assertions.assertEquals("https://biking.michael-simons.eu", response.getProviderUrl());
	Assertions.assertEquals("Aachen - Maastricht - Aachen", response.getTitle());
	Assertions.assertEquals("rich", response.getType());
	Assertions.assertEquals("1.0", response.getVersion());
	Assertions.assertEquals("testCache", oembedService.getCacheName());

	verify(cacheManager).addCacheIfAbsent("testCache");
	verify(cacheManager).cacheExists(OembedService.class.getName());
	verify(cache).get(embeddableUrl);
	Mockito.verifyNoMoreInteractions(cache, cacheManager);
	Mockito.verifyNoInteractions(defaultHttpClient);
    }

    /**
     * Handle invalid content gracecully and also add to cache
     * @throws IOException
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
	when(r.getStatusLine().getStatusCode()).thenReturn(200);
	when(r.getEntity().getContentType()).thenReturn(null);
	when(r.getEntity().getContent()).thenReturn(new ByteArrayInputStream("Hallo, Welt".getBytes()));

	when(defaultHttpClient.execute(any(HttpGet.class))).thenReturn(r);

	Ehcache cache = Mockito.mock(Ehcache.class);
	when(cache.get(embeddableUrl)).thenReturn(null);
	when(cacheManager.addCacheIfAbsent("testCache")).thenReturn(cache);

	OembedService oembedService = new OembedService(defaultHttpClient, cacheManager, Arrays.asList(oembedEndpoint), null);
	oembedService.setCacheName("testCache");
	Assertions.assertFalse(oembedService.getOembedResponseFor(embeddableUrl).isPresent());
	ArgumentCaptor<HttpGet> argumentCaptor = ArgumentCaptor.forClass(HttpGet.class);
	verify(defaultHttpClient).execute(argumentCaptor.capture());
	Assertions.assertEquals("https://biking.michael-simons.eu/oembed?format=json&url=https%3A%2F%2Fbiking.michael-simons.eu%2Ftracks%2F1&maxwidth=480&maxheight=360", argumentCaptor.getValue().getURI().toString());

	verify(cacheManager, times(2)).addCacheIfAbsent("testCache");
	verify(cacheManager).cacheExists(OembedService.class.getName());
	verify(cache).get(embeddableUrl);
	verify(cache).put(any(Element.class));

	verifyNoMoreInteractions(cache, cacheManager, defaultHttpClient);
    }

    /**
     * Embedding through configured endpoint including request provider
     * @throws IOException
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
	when(r.getStatusLine().getStatusCode()).thenReturn(200);
	when(r.getEntity().getContentType()).thenReturn(null);
	when(r.getEntity().getContent()).thenReturn(new ByteArrayInputStream(responseString.getBytes()));

	when(defaultHttpClient.execute(any(HttpGet.class))).thenReturn(r);

	Ehcache cache = Mockito.mock(Ehcache.class);
	when(cache.get(embeddableUrl)).thenReturn(null);
	when(cacheManager.addCacheIfAbsent("testCache")).thenReturn(cache);

	OembedService oembedService = new OembedService(defaultHttpClient, cacheManager, List.of(oembedEndpoint), null);
	oembedService.setCacheName("testCache");
	Assertions.assertTrue(oembedService.getOembedResponseFor(embeddableUrl).isPresent());
	ArgumentCaptor<HttpGet> argumentCaptor = ArgumentCaptor.forClass(HttpGet.class);
	verify(defaultHttpClient).execute(argumentCaptor.capture());
	Assertions.assertEquals("https://biking.michael-simons.eu/oembed?format=json&url=https%3A%2F%2Fbiking.michael-simons.eu%2Ftracks%2F1&maxwidth=480&maxheight=360", argumentCaptor.getValue().getURI().toString());

	verify(cacheManager, times(2)).addCacheIfAbsent("testCache");
	verify(cacheManager).cacheExists(OembedService.class.getName());
	verify(cache).get(embeddableUrl);
	verify(cache).put(any(Element.class));

	verifyNoMoreInteractions(cache, cacheManager, defaultHttpClient);
    }

    /**
     * Embedding through auto discovered endpoint using default request provider
     * @throws IOException
     */
    @Test
    public void getOembedResponseForShouldWork5() throws IOException {
	String embeddableUrl = "https://dailyfratze.de/michael/2014/10/13";
	HttpResponse r1 = Mockito.mock(HttpResponse.class, Mockito.RETURNS_DEEP_STUBS);
	when(r1.getStatusLine().getStatusCode()).thenReturn(200);
	when(r1.getEntity().getContentType()).thenReturn(null);
	when(r1.getEntity().getContent()).thenReturn(this.getClass().getResourceAsStream("/ac/simons/oembed/autodiscovery2.html"));

	HttpResponse r2 = Mockito.mock(HttpResponse.class, Mockito.RETURNS_DEEP_STUBS);
	when(r2.getStatusLine().getStatusCode()).thenReturn(200);
	when(r2.getEntity().getContentType()).thenReturn(null);
	when(r2.getEntity().getContent()).thenReturn(new ByteArrayInputStream(responseString.getBytes()));

	when(defaultHttpClient.execute(any(HttpGet.class))).thenAnswer((Answer) invocation -> {
		final String url = invocation.<HttpGet>getArgument(0).getURI().toString();
		HttpResponse rv = null;
		if(embeddableUrl.equals(url)) {
		rv = r1;
		} else if("https://dailyfratze.de/app/oembed.json?url=https%3A%2F%2Fdailyfratze.de%2Fmichael%2F2014%2F10%2F13".equals(url)) {
		rv = r2;
		}
		return rv;
	});

	OembedService oembedService = new OembedService(defaultHttpClient, null, new ArrayList<>(), null);
	oembedService.setAutodiscovery(true);

	Assertions.assertTrue(oembedService.getOembedResponseFor(embeddableUrl).isPresent());
	verify(defaultHttpClient, times(2)).execute(any(HttpGet.class));
	verifyNoMoreInteractions(cacheManager, defaultHttpClient);
    }

    @Test
    public void embedUrlsShouldWork1() {
	OembedService oembedService = new OembedService(defaultHttpClient, null, new ArrayList<>(), null);
	Assertions.assertNull(oembedService.embedUrls(null, null));
	Assertions.assertEquals("", oembedService.embedUrls("", null));
	Assertions.assertEquals("	", oembedService.embedUrls("	", null));
	Assertions.assertEquals(" ", oembedService.embedUrls(" ", null));
    }

    /**
     * Get also the "orElse" branch in selecting the renderer as no endpoint is configured, also no renderes
     */
    @Test
    public void embedUrlsShouldWork2() {
	Ehcache cache = Mockito.mock(Ehcache.class);
	String embeddableUrl = "https://biking.michael-simons.eu/tracks/1";
	when(cache.get(embeddableUrl)).thenReturn(new Element(embeddableUrl, response1));
	when(cacheManager.addCacheIfAbsent("testCache")).thenReturn(cache);

	OembedService oembedService = new OembedService(defaultHttpClient, cacheManager, new ArrayList<>(), null);
	oembedService.setCacheName("testCache");

	String in = "<p>Vor langer Zeit fuhr ich diesen Weg: <a href=\"https://biking.michael-simons.eu/tracks/1\">von Aachen nach Maastricht und zurück</a>.</p>";
	String expected = "<p>Vor langer Zeit fuhr ich diesen Weg: <iframe width=\"1024\" height=\"576\" src=\"https://biking.michael-simons.eu/tracks/1/embed?width=1024&height=576\" class=\"bikingTrack\"></iframe>.</p>";

	Assertions.assertEquals(expected, oembedService.embedUrls(in, null));
    }

    /**
     * <ul>
     * <li>Broken renderer</li>
     * <li>No oembed response for test.com</li>
     * </ul>
     */
    @Test
    public void embedUrlsShouldWork3() {
	Ehcache cache = Mockito.mock(Ehcache.class);
	String embeddableUrl = "https://biking.michael-simons.eu/tracks/1";
	when(cache.get(embeddableUrl)).thenReturn(new Element(embeddableUrl, response1));
	when(cacheManager.addCacheIfAbsent("testCache")).thenReturn(cache);

	OembedEndpoint oembedEndpoint = new OembedEndpoint();
	oembedEndpoint.setName("biking");
	oembedEndpoint.setEndpoint("https://biking.michael-simons.eu/oembed");
	oembedEndpoint.setMaxWidth(480);
	oembedEndpoint.setMaxHeight(360);
	oembedEndpoint.setUrlSchemes(Arrays.asList("https://biking\\.michael-simons\\.eu/tracks/.*"));
	oembedEndpoint.setResponseRendererClass(BrokenRenderer.class);

	OembedService oembedService = new OembedService(defaultHttpClient, cacheManager, Arrays.asList(oembedEndpoint), null);
	oembedService.setCacheName("testCache");

	String in = "<p>Vor langer Zeit fuhr ich diesen Weg: <a href=\"https://biking.michael-simons.eu/tracks/1\">von Aachen nach Maastricht und zurück</a>. Hier der Bericht: <a href=\"http://test.com\">Bericht</a>.</p>";
	String expected = in;

	Assertions.assertEquals(expected, oembedService.embedUrls(in, null));
    }

    @Test
    public void embedUrlsShouldWork4() {

	OembedService oembedService = new OembedService(defaultHttpClient, null, new ArrayList<>(), null);
	Assertions.assertThrowsExactly(OembedException.class, () ->
	oembedService.embedUrls(null, null, Integer.class), "Invalid target class: java.lang.Integer");
    }

    @Test
    public void misc() {
	OembedService oembedService = new OembedService(defaultHttpClient, cacheManager, new ArrayList<>(), null);
	Assertions.assertEquals(3600, oembedService.getDefaultCacheAge());
	oembedService.setDefaultCacheAge(10);
	Assertions.assertEquals(10, oembedService.getDefaultCacheAge());
    }
}
