/*
 * Copyright 2014 michael-simons.eu.
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

import java.io.InputStream;
import java.io.OutputStream;

/**
 * This is 1:1 copy from the interface of the same name from
 * <a href="https://github.com/michael-simons/java-oembed">java-oembed</a>
 * except for the checked exceptions.
 *
 * @author Michael J. Simons, 2010-12-24
 */
public interface OembedParser {

    /**
     * Unmarshals an {@link OembedResponse} from the given inputstream
     *
     * @param in
     * @return A full OembedResponse
     */
    public OembedResponse unmarshal(final InputStream in);

    /**
     * Marshals the given {@link OembedResponse} {@code oembedResponse} into the
     * OutputStream {@code out}.
     *
     * @param oembedResponse
     * @param out
     */
    public void marshal(final OembedResponse oembedResponse, final OutputStream out);
}
