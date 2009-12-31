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

import java.io.Serializable;

import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;
import oauth.signpost.http.HttpRequest;

/**
 * Exposes a simple interface to sign HTTP requests using a given OAuth token
 * and secret.
 * 
 * @author Matthias Kaeppler
 * 
 */
public interface OAuthConsumer extends Serializable {

    /**
     * Signs the given HTTP request by writing an OAuth signature string to the
     * request's Authorization header.
     * 
     * @param request
     *        the request to sign
     * @return the request object passed as an argument
     * @throws OAuthMessageSignerException
     * @throws OAuthExpectationFailedException
     * @throws OAuthCommunicationException
     */
    public HttpRequest sign(HttpRequest request) throws OAuthMessageSignerException,
            OAuthExpectationFailedException, OAuthCommunicationException;

	/**
     * Signs the given HTTP request by writing an OAuth signature string to the
     * request's Authorization header. This method accepts adapted requests; the
     * consumer implementation must ensure that only those request types are
     * passed which it supports.
     * 
     * @param request
     *        the request to sign
     * @return the request object passed as an argument
     * @throws OAuthMessageSignerException
     * @throws OAuthExpectationFailedException
     * @throws OAuthCommunicationException
     */
	public HttpRequest sign(Object request) throws OAuthMessageSignerException,
            OAuthExpectationFailedException, OAuthCommunicationException;

	public void setTokenWithSecret(String token, String tokenSecret);

	public String getToken();

	public String getTokenSecret();

	public String getConsumerKey();

	public String getConsumerSecret();
}
