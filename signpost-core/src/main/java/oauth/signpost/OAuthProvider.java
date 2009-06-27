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

import java.util.Map;

import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;
import oauth.signpost.exception.OAuthNotAuthorizedException;

public interface OAuthProvider {

    /**
     * Queries the service provider for a request token.
     * <p>
     * <b>Pre-conditions:</b> the {@link AbstractOAuthConsumer} connected to
     * this provider must have a valid consumer key and consumer secret already
     * set.
     * </p>
     * <p>
     * <b>Post-conditions:</b> the {@link AbstractOAuthConsumer} connected to
     * this provider will have an unauthorized request token and token secret
     * set.
     * </p>
     * 
     * @param callbackUrl
     *            Pass an actual URL if your app can receive callbacks and you
     *            want to get informed about the result of the authorization
     *            process. Pass {@link OAuth.OUT_OF_BAND} if the service
     *            provider implements OAuth 1.0a and your app cannot receive
     *            callbacks. Pass null if the service provider implements OAuth
     *            1.0 and your app cannot receive callbacks.
     * @return The URL to which the user must be sent in order to authorize the
     *         consumer. It include the unauthorized request token and the
     *         callback URL.
     * @throws OAuthMessageSignerException
     *             if signing the request failed
     * @throws OAuthNotAuthorizedException
     *             if the service provider rejected the consumer
     * @throws OAuthExpectationFailedException
     *             if required parameters were not correctly set by the consumer
     *             or service provider
     * @throws OAuthCommunicationException
     *             if server communication failed
     */
    public String retrieveRequestToken(String callbackUrl)
            throws OAuthMessageSignerException, OAuthNotAuthorizedException,
            OAuthExpectationFailedException, OAuthCommunicationException;

    /**
     * Queries the service provider for an access token.
     * <p>
     * <b>Pre-conditions:</b> the {@link AbstractOAuthConsumer} connected to
     * this provider must have a valid consumer key, consumer secret, authorized
     * request token and token secret already set.
     * </p>
     * <p>
     * <b>Post-conditions:</b> the {@link AbstractOAuthConsumer} connected to
     * this provider will have an access token and token secret set.
     * </p>
     * 
     * @param oauthVerifier
     *            <b>NOTE: Only applies to service providers implementing OAuth
     *            1.0a. Set to null if the service provider is still using OAuth
     *            1.0.</b> The verification code issued by the service provider
     *            after the the user has granted the consumer authorization. If
     *            the callback method provided in the previous step was
     *            {@link OAuth.OUT_OF_BAND}, then you must ask the user for
     *            this value. If your app has received a callback, the
     *            verfication code was passed as part of that request instead.
     * @throws OAuthMessageSignerException
     *             if signing the request failed
     * @throws OAuthNotAuthorizedException
     *             if the service provider rejected the consumer
     * @throws OAuthExpectationFailedException
     *             if required parameters were not correctly set by the consumer
     *             or service provider
     * @throws OAuthCommunicationException
     *             if server communication failed
     */
    public void retrieveAccessToken(String oauthVerifier)
            throws OAuthMessageSignerException, OAuthNotAuthorizedException,
            OAuthExpectationFailedException, OAuthCommunicationException;

    /**
     * Any additional non-OAuth parameters returned in the response body of a
     * token request can be obtained through this method. These parameters will
     * be preserved until the next token request is issued. The return value is
     * never null.
     */
    public Map<String, String> getResponseParameters();

    /**
     * @param isOAuth10aProvider
     *            set to true if the service provider supports OAuth 1.0a. Note
     *            that you need only call this method if you reconstruct a
     *            provider object in between calls to retrieveRequestToken() and
     *            retrieveAccessToken() (i.e. if the object state isn't
     *            preserved). If instead those two methods are called on the
     *            same provider instance, this flag will be deducted
     *            automatically based on the server response during
     *            retrieveRequestToken(), so you can simply ignore this method.
     */
    public void setOAuth10a(boolean isOAuth10aProvider);

    /**
     * @return true if the service provider supports OAuth 1.0a. Note that the
     *         value returned here is only meaningful after you have already
     *         performed the token handshake, otherwise there is no way to
     *         determine what version of the OAuth protocol the service provider
     *         implements.
     */
    public boolean isOAuth10a();
}
