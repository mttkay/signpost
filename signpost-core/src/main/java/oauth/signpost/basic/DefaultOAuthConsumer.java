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
package oauth.signpost.basic;

import java.net.HttpURLConnection;

import oauth.signpost.AbstractOAuthConsumer;
import oauth.signpost.http.HttpRequest;

/**
 * The default implementation for an OAuth consumer. Only supports signing
 * {@link java.net.HttpURLConnection} type requests.
 * 
 * @author Matthias Kaeppler
 */
public class DefaultOAuthConsumer extends AbstractOAuthConsumer {

    private static final long serialVersionUID = 1L;

    public DefaultOAuthConsumer(String consumerKey, String consumerSecret) {
        super(consumerKey, consumerSecret);
    }

    @Override
    protected HttpRequest wrap(Object request) {
        if (!(request instanceof HttpURLConnection)) {
            throw new IllegalArgumentException(
                    "The default consumer expects requests of type java.net.HttpURLConnection");
        }
        return new HttpURLConnectionRequestAdapter((HttpURLConnection) request);
    }

}
