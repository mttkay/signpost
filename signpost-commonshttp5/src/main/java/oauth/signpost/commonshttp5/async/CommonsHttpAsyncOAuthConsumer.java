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
package oauth.signpost.commonshttp5.async;

import oauth.signpost.AbstractOAuthConsumer;
import oauth.signpost.http.HttpRequest;
import org.apache.hc.client5.http.async.methods.SimpleHttpRequest;

/**
 * Supports signing HTTP requests of type {@link SimpleHttpRequest}.
 *
 * @author Kristof Jozsa
 */
public class CommonsHttpAsyncOAuthConsumer extends AbstractOAuthConsumer {

    private static final long serialVersionUID = 1L;

    public CommonsHttpAsyncOAuthConsumer(String consumerKey, String consumerSecret) {
        super(consumerKey, consumerSecret);
    }

    @Override
    protected HttpRequest wrap(Object request) {
        if (!(request instanceof SimpleHttpRequest)) {
            throw new IllegalArgumentException("This consumer expects requests of type " + SimpleHttpRequest.class.getCanonicalName());
        }

        return new HttpAsyncRequestAdapter((SimpleHttpRequest) request);
    }

}
