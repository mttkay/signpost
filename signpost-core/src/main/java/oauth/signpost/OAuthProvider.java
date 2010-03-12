/*
 * Copyright (c) 2009 Matthias Kaeppler Licensed under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law
 * or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package oauth.signpost;

import java.io.Serializable;
import java.util.Map;

import oauth.signpost.basic.DefaultOAuthConsumer;
import oauth.signpost.basic.DefaultOAuthProvider;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;
import oauth.signpost.exception.OAuthNotAuthorizedException;
import oauth.signpost.http.HttpParameters;

/**
 * <p>
 * Supplies an interface that can be used to retrieve request and access tokens
 * from an OAuth 1.0(a) service provider. A provider object requires an
 * {@link OAuthConsumer} to sign the token request message; after a token has
 * been retrieved, the consumer is automatically updated with the token and the
 * corresponding secret.
 * </p>
 * <p>
 * To initiate the token exchange, create a new provider instance and configure
 * it with the URLs the service provider exposes for requesting tokens and
 * resource authorization, e.g.:
 * </p>
 * 
 * <pre>
 * OAuthProvider provider = new DefaultOAuthProvider(&quot;http://twitter.com/oauth/request_token&quot;,
 *         &quot;http://twitter.com/oauth/access_token&quot;, &quot;http://twitter.com/oauth/authorize&quot;);
 * </pre>
 * <p>
 * Depending on the HTTP library you use, you may need a different provider
 * type, refer to the website documentation for how to do that.
 * </p>
 * <p>
 * To receive a request token which the user must authorize, you invoke it using
 * a consumer instance and a callback URL:
 * </p>
 * <p>
 * 
 * <pre>
 * String url = provider.retrieveRequestToken(consumer, &quot;http://www.example.com/callback&quot;);
 * </pre>
 * 
 * </p>
 * <p>
 * That url must be opened in a Web browser, where the user can grant access to
 * the resources in question. If that succeeds, the service provider will
 * redirect to the callback URL and append the blessed request token.
 * </p>
 * <p>
 * That token must now be exchanged for an access token, as such:
 * </p>
 * <p>
 * 
 * <pre>
 * provider.retrieveAccessToken(consumer, nullOrVerifierCode);
 * </pre>
 * 
 * </p>
 * <p>
 * where nullOrVerifierCode is either null if your provided a callback URL in
 * the previous step, or the pin code issued by the service provider to the user
 * if the request was out-of-band (cf. {@link OAuth#OUT_OF_BAND}.
 * </p>
 * <p>
 * The consumer used during token handshakes is now ready for signing.
 * </p>
 * 
 * @see DefaultOAuthProvider
 * @see DefaultOAuthConsumer
 * @see OAuthProviderListener
 */
public interface OAuthProvider extends Serializable {

    /**
     * Queries the service provider for a request token.
     * <p>
     * <b>Pre-conditions:</b> the given {@link OAuthConsumer} must have a valid
     * consumer key and consumer secret already set.
     * </p>
     * <p>
     * <b>Post-conditions:</b> the given {@link OAuthConsumer} will have an
     * unauthorized request token and token secret set.
     * </p>
     * 
     * @param consumer
     *        the {@link OAuthConsumer} that should be used to sign the request
     * @param callbackUrl
     *        Pass an actual URL if your app can receive callbacks and you want
     *        to get informed about the result of the authorization process.
     *        Pass {@link OAuth.OUT_OF_BAND} if the service provider implements
     *        OAuth 1.0a and your app cannot receive callbacks. Pass null if the
     *        service provider implements OAuth 1.0 and your app cannot receive
     *        callbacks. Please note that some services (among them Twitter)
     *        will fail authorization if you pass a callback URL but register
     *        your application as a desktop app (which would only be able to
     *        handle OOB requests).
     * @return The URL to which the user must be sent in order to authorize the
     *         consumer. It includes the unauthorized request token (and in the
     *         case of OAuth 1.0, the callback URL -- 1.0a clients send along
     *         with the token request).
     * @throws OAuthMessageSignerException
     *         if signing the request failed
     * @throws OAuthNotAuthorizedException
     *         if the service provider rejected the consumer
     * @throws OAuthExpectationFailedException
     *         if required parameters were not correctly set by the consumer or
     *         service provider
     * @throws OAuthCommunicationException
     *         if server communication failed
     */
    public String retrieveRequestToken(OAuthConsumer consumer, String callbackUrl)
            throws OAuthMessageSignerException, OAuthNotAuthorizedException,
            OAuthExpectationFailedException, OAuthCommunicationException;

    /**
     * Queries the service provider for an access token.
     * <p>
     * <b>Pre-conditions:</b> the given {@link OAuthConsumer} must have a valid
     * consumer key, consumer secret, authorized request token and token secret
     * already set.
     * </p>
     * <p>
     * <b>Post-conditions:</b> the given {@link OAuthConsumer} will have an
     * access token and token secret set.
     * </p>
     * 
     * @param consumer
     *        the {@link OAuthConsumer} that should be used to sign the request
     * @param oauthVerifier
     *        <b>NOTE: Only applies to service providers implementing OAuth
     *        1.0a. Set to null if the service provider is still using OAuth
     *        1.0.</b> The verification code issued by the service provider
     *        after the the user has granted the consumer authorization. If the
     *        callback method provided in the previous step was
     *        {@link OAuth.OUT_OF_BAND}, then you must ask the user for this
     *        value. If your app has received a callback, the verfication code
     *        was passed as part of that request instead.
     * @throws OAuthMessageSignerException
     *         if signing the request failed
     * @throws OAuthNotAuthorizedException
     *         if the service provider rejected the consumer
     * @throws OAuthExpectationFailedException
     *         if required parameters were not correctly set by the consumer or
     *         service provider
     * @throws OAuthCommunicationException
     *         if server communication failed
     */
    public void retrieveAccessToken(OAuthConsumer consumer, String oauthVerifier)
            throws OAuthMessageSignerException, OAuthNotAuthorizedException,
            OAuthExpectationFailedException, OAuthCommunicationException;

    /**
     * Any additional non-OAuth parameters returned in the response body of a
     * token request can be obtained through this method. These parameters will
     * be preserved until the next token request is issued. The return value is
     * never null.
     */
    public HttpParameters getResponseParameters();

    /**
     * Subclasses must use this setter to preserve any non-OAuth query
     * parameters contained in the server response. It's the caller's
     * responsibility that any OAuth parameters be removed beforehand.
     * 
     * @param parameters
     *        the map of query parameters served by the service provider in the
     *        token response
     */
    public void setResponseParameters(HttpParameters parameters);

    /**
     * Use this method to set custom HTTP headers to be used for the requests
     * which are sent to retrieve tokens. @deprecated THIS METHOD HAS BEEN
     * DEPRECATED. Use {@link OAuthProviderListener} to customize requests.
     * 
     * @param header
     *        The header name (e.g. 'WWW-Authenticate')
     * @param value
     *        The header value (e.g. 'realm=www.example.com')
     */
    @Deprecated
    public void setRequestHeader(String header, String value);

    /**
     * @deprecated THIS METHOD HAS BEEN DEPRECATED. Use
     *             {@link OAuthProviderListener} to customize requests.
     * @return all request headers set via {@link #setRequestHeader}
     */
    @Deprecated
    public Map<String, String> getRequestHeaders();

    /**
     * @param isOAuth10aProvider
     *        set to true if the service provider supports OAuth 1.0a. Note that
     *        you need only call this method if you reconstruct a provider
     *        object in between calls to retrieveRequestToken() and
     *        retrieveAccessToken() (i.e. if the object state isn't preserved).
     *        If instead those two methods are called on the same provider
     *        instance, this flag will be deducted automatically based on the
     *        server response during retrieveRequestToken(), so you can simply
     *        ignore this method.
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

    public String getRequestTokenEndpointUrl();

    public String getAccessTokenEndpointUrl();

    public String getAuthorizationWebsiteUrl();

    public void setListener(OAuthProviderListener listener);

    public void removeListener(OAuthProviderListener listener);
}
