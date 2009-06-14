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
package oauth.signpost;

import oauth.signpost.exception.OAuthMessageSignerException;
import oauth.signpost.http.HttpRequest;

public interface OAuthConsumer {

    public HttpRequest sign(HttpRequest request)
            throws OAuthMessageSignerException;

    public HttpRequest sign(Object request) throws OAuthMessageSignerException;

    public void setTokenWithSecret(String token, String tokenSecret);

    public String getToken();

    public String getTokenSecret();

    public String getConsumerKey();

    public String getConsumerSecret();
}
