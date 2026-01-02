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

import java.io.InputStream;
import java.io.OutputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

/**
 * Provides XML Parsing for {@link OembedResponse}s.
 *
 * @author Michael J. Simons
 * @since 2010-12-24
 */
final class OembedXmlParser implements OembedParser {

	/**
	 * The JAXB context for parsing XML Oembed responses.
	 */
	private final JAXBContext jaxbContext;

	/**
	 * Creates a new OembedJsonParser. It can throw an exception if the JAXB context
	 * cannot be initialized.
	 */
	OembedXmlParser() {
		try {
			this.jaxbContext = JAXBContext.newInstance(OembedResponse.class);
		}
		catch (JAXBException ex) {
			// Ignore this... I have no clue how that should happen.
			throw new OembedException(ex);
		}
	}

	@Override
	public OembedResponse unmarshal(final InputStream in) {
		try {
			return (OembedResponse) this.jaxbContext.createUnmarshaller().unmarshal(in);
		}
		catch (JAXBException ex) {
			throw new OembedException(ex);
		}
	}

	@Override
	public void marshal(final OembedResponse oembedResponse, final OutputStream out) {
		try {
			this.jaxbContext.createMarshaller().marshal(oembedResponse, out);
		}
		catch (JAXBException ex) {
			throw new OembedException(ex);
		}
	}

}
