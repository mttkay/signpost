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

import java.util.HashMap;
import java.util.Map;

import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;
import oauth.signpost.exception.OAuthNotAuthorizedException;

/**
 * ABC for all provider implementations. If you're writing a custom provider,
 * you will probably inherit from this class, since it takes a lot of work from
 * you.
 * 
 * @author Matthias Kaeppler
 */
public abstract class AbstractOAuthProvider implements OAuthProvider {

    private static final long serialVersionUID = 1L;

    private String requestTokenEndpointUrl;

	private String accessTokenEndpointUrl;

	private String authorizationWebsiteUrl;

	private Map<String, String> responseParameters;

	private Map<String, String> defaultHeaders;

	private boolean isOAuth10a;

    public AbstractOAuthProvider(String requestTokenEndpointUrl, String accessTokenEndpointUrl,
            String authorizationWebsiteUrl) {
        this.requestTokenEndpointUrl = requestTokenEndpointUrl;
        this.accessTokenEndpointUrl = accessTokenEndpointUrl;
        this.authorizationWebsiteUrl = authorizationWebsiteUrl;
        this.responseParameters = new HashMap<String, String>();
        this.defaultHeaders = new HashMap<String, String>();
    }

    public String retrieveRequestToken(OAuthConsumer consumer, String callbackUrl)
            throws OAuthMessageSignerException, OAuthNotAuthorizedException,
            OAuthExpectationFailedException, OAuthCommunicationException {

        // invalidate current credentials, if any
        consumer.setTokenWithSecret(null, null);

        // 1.0a expects the callback to be sent while getting the request token.
        // 1.0 service providers would simply ignore this parameter.
        retrieveToken(consumer, OAuth.addQueryParameters(requestTokenEndpointUrl,
            OAuth.OAUTH_CALLBACK, callbackUrl));

        String callbackConfirmed = responseParameters.get(OAuth.OAUTH_CALLBACK_CONFIRMED);
        responseParameters.remove(OAuth.OAUTH_CALLBACK_CONFIRMED);
        isOAuth10a = Boolean.TRUE.toString().equals(callbackConfirmed);

        // 1.0 service providers expect the callback as part of the auth URL,
        // Do not send when 1.0a.
        if (isOAuth10a) {
            return OAuth.addQueryParameters(authorizationWebsiteUrl, OAuth.OAUTH_TOKEN, consumer
                .getToken());
        } else {
            return OAuth.addQueryParameters(authorizationWebsiteUrl, OAuth.OAUTH_TOKEN, consumer
                .getToken(), OAuth.OAUTH_CALLBACK, callbackUrl);
        }
    }

    public void retrieveAccessToken(OAuthConsumer consumer, String oauthVerifier)
            throws OAuthMessageSignerException, OAuthNotAuthorizedException,
            OAuthExpectationFailedException, OAuthCommunicationException {

        if (consumer.getToken() == null || consumer.getTokenSecret() == null) {
            throw new OAuthExpectationFailedException(
                "Authorized request token or token secret not set. "
                        + "Did you retrieve an authorized request token before?");
        }

        String endpointUrl = isOAuth10a && oauthVerifier != null ? OAuth.addQueryParameters(
            accessTokenEndpointUrl, OAuth.OAUTH_VERIFIER, oauthVerifier) : accessTokenEndpointUrl;

        retrieveToken(consumer, endpointUrl);
    }

    /**
     * <p>
     * Implemented by subclasses. The responsibility of this method is to
     * contact the service provider at the given endpoint URL and fetch a
     * request or access token. What kind of token is retrieved solely depends
     * on the URL being used.
     * </p>
     * <p>
     * Correct implementations of this method must guarantee the following
     * post-conditions:
     * <ul>
     * <li>the {@link OAuthConsumer} passed to this method must have a valid
     * {@link OAuth#OAUTH_TOKEN} and {@link OAuth#OAUTH_TOKEN_SECRET} set by
     * calling {@link OAuthConsumer#setTokenWithSecret(String, String)}</li>
     * <li>{@link #getResponseParameters()} must return the set of query
     * parameters served by the service provider in the token response, with all
     * OAuth specific parameters being removed</li>
     * </ul>
     * </p>
     * 
     * @param consumer
     *        the {@link OAuthConsumer} that should be used to sign the request
     * @param endpointUrl
     *        the URL at which the service provider serves the OAuth token that
     *        is to be fetched
     * @throws OAuthMessageSignerException
     *         if signing the token request fails
     * @throws OAuthCommunicationException
     *         if a network communication error occurs
     * @throws OAuthNotAuthorizedException
     *         if the server replies 401 - Unauthorized
     * @throws OAuthExpectationFailedException
     *         if an expectation has failed, e.g. because the server didn't
     *         reply in the expected format
     */
    protected abstract void retrieveToken(OAuthConsumer consumer, String endpointUrl)
			throws OAuthMessageSignerException, OAuthCommunicationException,
			OAuthNotAuthorizedException, OAuthExpectationFailedException;
	
	public Map<String, String> getResponseParameters() {
		return responseParameters;
	}

    /**
     * Returns a single query parameter as served by the service provider in a
     * token reply. You must call {@link #setResponseParameters} with the set of
     * parameters before using this method.
     * 
     * @param key
     *        the parameter name
     * @return the parameter value
     */
    protected String getResponseParameter(String key) {
        return responseParameters.get(key);
    }

    public void setResponseParameters(Map<String, String> parameters) {
        this.responseParameters = parameters;
    }

	public void setOAuth10a(boolean isOAuth10aProvider) {
		this.isOAuth10a = isOAuth10aProvider;
	}

	public boolean isOAuth10a() {
		return isOAuth10a;
	}

	public String getRequestTokenEndpointUrl() {
		return this.requestTokenEndpointUrl;
	}

	public String getAccessTokenEndpointUrl() {
		return this.accessTokenEndpointUrl;
	}

	public String getAuthorizationWebsiteUrl() {
		return this.authorizationWebsiteUrl;
	}

	public void setRequestHeader(String header, String value) {
		defaultHeaders.put(header, value);
	}

    public Map<String, String> getRequestHeaders() {
        return defaultHeaders;
    }
}
