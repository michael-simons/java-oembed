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

import org.jsoup.nodes.Element;

/**
 * The purpose of this interface is turning {@link OembedResponse}s into usable
 * chunks of html code.
 *
 * @author Michael J. Simons, 2014-12-31
 */
public interface OembedResponseRenderer {

    /**
     * Renderes the {@link OembedResponse} {@code response} into an html string.
     * The original anchor is provided for giving more context information.
     *
     * @param response The response that should be handled
     * @param originalAnchor The anchor that triggered the oembed request. This
     * is a clone of the original object, changes will not be propagated.
     * @return An html fragment containt the representation of the given
     * OembedResponse
     */
    public String render(OembedResponse response, Element originalAnchor);
}
