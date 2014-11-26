package ac.simons.tests.oembed;

import org.junit.Assert;
import org.junit.Test;

import ac.simons.oembed.OembedException;
import ac.simons.oembed.OembedResponse;
import ac.simons.oembed.OembedXmlParser;

/**
 * @author Michael J. Simons, 2014-11-26
 */
public class XMLWithCDataTest {

	@Test
	public void cdataShouldBeRemoved() throws OembedException {
		final OembedXmlParser oembedXmlParser = new OembedXmlParser();
		OembedResponse response = oembedXmlParser.unmarshal(this.getClass().getResourceAsStream("/soundcloud_fixed.xml"));
		Assert.assertEquals("<iframe width=\"100%\" height=\"400\" scrolling=\"no\" frameborder=\"no\" src=\"https://w.soundcloud.com/player/?visual=true&amp;url=http%3A%2F%2Fapi.soundcloud.com%2Ftracks%2F150745932&amp;show_artwork=true&amp;amp=\"></iframe>", response.getHtml());
	}

}
