/* Copyright (c) 2009 Matthias Kaeppler
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package oauth.signpost.commonshttp5;

import oauth.signpost.AbstractOAuthConsumer;
import oauth.signpost.http.HttpRequest;
import org.apache.hc.client5.http.classic.methods.HttpUriRequest;
import org.apache.hc.core5.http.ClassicHttpRequest;

/**
 * Supports signing HTTP requests of type {@link HttpUriRequest}.
 *
 * @author Matthias Kaeppler
 */
public class CommonsHttpOAuthConsumer extends AbstractOAuthConsumer {

    private static final long serialVersionUID = 1L;

    public CommonsHttpOAuthConsumer(String consumerKey, String consumerSecret) {
        super(consumerKey, consumerSecret);
    }

    @Override
    protected HttpRequest wrap(Object request) {
        if (!(request instanceof ClassicHttpRequest)) {
            throw new IllegalArgumentException(
                    "This consumer expects requests of type "
                            + ClassicHttpRequest.class.getCanonicalName());
        }

        return new HttpRequestAdapter((ClassicHttpRequest) request);
    }

}
