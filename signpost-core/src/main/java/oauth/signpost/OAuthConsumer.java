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
import oauth.signpost.http.RequestParameters;
import oauth.signpost.signature.OAuthMessageSigner;
import oauth.signpost.signature.SigningStrategy;

/**
 * Exposes a simple interface to sign HTTP requests using a given OAuth token
 * and secret.
 * 
 * @author Matthias Kaeppler
 * 
 */
public interface OAuthConsumer extends Serializable {

    /**
     * Sets the message signer that should be used to generate the OAuth
     * signature.
     * 
     * @param messageSigner
     *        the signer
     */
    public void setMessageSigner(OAuthMessageSigner messageSigner);
    
    /**
     * Defines which strategy should be used to write a signature to an
     * HTTP request.
     * @param signingStrategy the strategy
     */
    public void setSigningStrategy(SigningStrategy signingStrategy);

    /**
     * <p>
     * If you're seeing 401s during calls to
     * {@link OAuthProvider#retrieveRequestToken}, try setting this to true.
     * </p>
     * 
     * @param enable
     *        true or false
     */
    public void setSendEmptyTokens(boolean enable);

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

    /**
     * <p>
     * "Signs" the given URL by appending all OAuth parameters to it which are
     * required for message signing. The assumed HTTP method is GET.
     * Essentially, this is equivalent to signing an HTTP GET request, but it
     * can be useful if your application requires clickable links to protected
     * resources, i.e. when your application does not have access to the actual
     * request that is being sent.
     * </p>
     * 
     * @param url
     *        the input URL. May have query parameters.
     * @return the input URL, with all necessary OAuth parameters attached as a
     *         query string. Existing query parameters are preserved.
     * @throws OAuthMessageSignerException
     * @throws OAuthExpectationFailedException
     * @throws OAuthCommunicationException
     */
    public String sign(String url) throws OAuthMessageSignerException,
            OAuthExpectationFailedException, OAuthCommunicationException;

	public void setTokenWithSecret(String token, String tokenSecret);

	public String getToken();

	public String getTokenSecret();

	public String getConsumerKey();

	public String getConsumerSecret();

    /**
     * Returns all parameters collected from the HTTP request during message
     * signing (this means the return value may be NULL before a call to
     * {@link #sign}), plus all required OAuth parameters that were added
     * because the request didn't contain them beforehand. In other words, this
     * is the set of parameters that were used for creating the message
     * signature.
     * 
     * @return the request parameters used for message signing
     */
    public RequestParameters getRequestParameters();
}
