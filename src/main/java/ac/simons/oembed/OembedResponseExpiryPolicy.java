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

import java.time.Duration;
import java.util.function.Supplier;

import org.ehcache.expiry.ExpiryPolicy;

/**
 * A custom {@link ExpiryPolicy} for Ehcache 3.x that extracts the TTL from an
 * {@link OembedResponse}, enabling per-entry expiration times.
 *
 * @author Oliver Lockwood
 * @author Michael J. Simons
 * @since 2026-01-17
 */
final class OembedResponseExpiryPolicy implements ExpiryPolicy<String, OembedResponseWrapper> {

	/**
	 * Time in seconds responses are cached. Used if the response has no cache_age.
	 */
	private long defaultCacheAge = 3600;

	@Override
	public Duration getExpiryForCreation(String key, OembedResponseWrapper value) {
		return getExpiryOf(value);
	}

	@Override
	public Duration getExpiryForAccess(String key, Supplier<? extends OembedResponseWrapper> value) {
		return null;
	}

	@Override
	public Duration getExpiryForUpdate(String key, Supplier<? extends OembedResponseWrapper> oldValue,
			OembedResponseWrapper newValue) {
		return getExpiryOf(newValue);
	}

	Duration getExpiryOf(OembedResponseWrapper wrapper) {
		long cacheAge;
		if (wrapper.value() != null && wrapper.value().getCacheAge() != null) {
			// Cache at least 60 seconds
			cacheAge = Math.max(60, wrapper.value().getCacheAge());
		}
		else {
			cacheAge = this.defaultCacheAge;
		}
		return Duration.ofSeconds(Math.min(cacheAge, Integer.MAX_VALUE));
	}

	long getDefaultCacheAge() {
		return this.defaultCacheAge;
	}

	void setDefaultCacheAge(long defaultCacheAge) {
		this.defaultCacheAge = defaultCacheAge;
	}

}
