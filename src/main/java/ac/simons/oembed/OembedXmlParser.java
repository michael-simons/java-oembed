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
package ac.simons.oembed;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

/**
 * @author Michael J. Simons
 */
public class OembedXmlParser implements OembedParser {
	private final JAXBContext jaxbContext;
	
	public OembedXmlParser() {
		try {
			this.jaxbContext = JAXBContext.newInstance(OembedResponse.class);
		} catch(JAXBException e) {
			throw new RuntimeException(e);
		}
	}
	

	@Override
	public OembedResponse unmarshal(InputStream httpResponse) throws OembedException {
		try {
			return (OembedResponse) jaxbContext.createUnmarshaller().unmarshal(httpResponse);
		} catch (JAXBException e) {
			throw new OembedException(e);
		}
	}


	@Override
	public String marshal(OembedResponse oembedResponse) throws OembedException {
		try {
			final StringWriter out = new StringWriter();
			jaxbContext.createMarshaller().marshal(oembedResponse, out);
			out.flush();
			out.close();
			return out.toString();
		} catch (Exception e) {
			throw new OembedException(e);
		}
	}

	@Override
	public void marshal(OembedResponse oembedResponse, OutputStream outputStream) throws OembedException {
		try {
			jaxbContext.createMarshaller().marshal(oembedResponse, outputStream);
		} catch (Exception e) {
			throw new OembedException(e);
		}
	}
}