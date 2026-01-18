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

import java.io.Serializable;

/**
 * A wrapper class for caching {@link OembedResponse} objects with per-entry TTL support.
 * Ehcache 3.x doesn't support per-element TTL like Ehcache 2.x did, so this wrapper
 * stores the TTL in seconds which is used by {@link CachedOembedResponseExpiryPolicy}.
 *
 * @author Michael J. Simons
 * @since 2026-01-17
 */
public final class CachedOembedResponse implements Serializable {

	private static final long serialVersionUID = 1L;

	private final OembedResponse response;

	private final int ttlSeconds;

	/**
	 * Creates a new cached response wrapper.
	 * @param response the oembed response, may be null for failed lookups
	 * @param ttlSeconds the time-to-live in seconds
	 */
	public CachedOembedResponse(OembedResponse response, int ttlSeconds) {
		this.response = response;
		this.ttlSeconds = ttlSeconds;
	}

	/**
	 * {@return the wrapped oembed response, may be null}
	 */
	public OembedResponse getResponse() {
		return this.response;
	}

	/**
	 * {@return the time-to-live in seconds for this cache entry}
	 */
	public int getTtlSeconds() {
		return this.ttlSeconds;
	}

}
