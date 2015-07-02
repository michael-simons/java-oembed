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

import org.jsoup.nodes.Element;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

/**
 *
 * @author Michael J. Simons, 2015-07-02
 */
public class DefaultOembedResponseRendererTest {

    @Test
    public void renderShouldWorkForPhotos() {
	final DefaultOembedResponseRenderer renderer = new DefaultOembedResponseRenderer();
	OembedResponse response;
	response = new OembedResponse();
	response.setType("photo");
	response.setUrl("http://info.michael-simons.eu/wp-content/uploads/2015/05/Blogcake.jpeg");
	response.setWidth(200);
	response.setHeight(100);
	Assert.assertEquals("<img src=\"http://info.michael-simons.eu/wp-content/uploads/2015/05/Blogcake.jpeg\" style=\"width:200px; height:100px;\" alt=\"\" title=\"\"/>", renderer.render(response, null));
	response.setTitle("test");
	Assert.assertEquals("<img src=\"http://info.michael-simons.eu/wp-content/uploads/2015/05/Blogcake.jpeg\" style=\"width:200px; height:100px;\" alt=\"test\" title=\"test\"/>", renderer.render(response, null));
    }

    @Test
    public void renderShouldWorkForVideos() {
	final DefaultOembedResponseRenderer renderer = new DefaultOembedResponseRenderer();
	OembedResponse response;
	response = new OembedResponse();
	response.setType("video");
	response.setHtml("<html />");
	Assert.assertEquals("<html />", renderer.render(response, null));
    }
    
    @Test
    public void renderShouldWorkForLink() {		
	final DefaultOembedResponseRenderer renderer = new DefaultOembedResponseRenderer();
	final Element originalAnchor = Mockito.mock(Element.class);
	Mockito.when(originalAnchor.absUrl("href")).thenReturn("http://info.michael-simons.eu");
	OembedResponse response;
	response = new OembedResponse();
	response.setType("link");	
	Assert.assertEquals("<a href=\"http://info.michael-simons.eu\">http://info.michael-simons.eu</a>", renderer.render(response, originalAnchor));
	response.setTitle("title");	
	response.setUrl("http://info.michael-simons.eu/2015/05/24/java20/");
	Assert.assertEquals("<a href=\"http://info.michael-simons.eu/2015/05/24/java20/\">title</a>", renderer.render(response, originalAnchor));
    }
    
    @Test
    public void renderShouldWorkForRich() {
	final DefaultOembedResponseRenderer renderer = new DefaultOembedResponseRenderer();
	OembedResponse response;
	response = new OembedResponse();
	response.setType("rich");
	response.setHtml("<html />");
	Assert.assertEquals("<html />", renderer.render(response, null));
    }
}
