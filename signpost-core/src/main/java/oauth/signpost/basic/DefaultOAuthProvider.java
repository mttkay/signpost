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
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import oauth.signpost.OAuth;
import oauth.signpost.OAuthConsumer;
import oauth.signpost.OAuthProvider;
import oauth.signpost.Parameter;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;
import oauth.signpost.exception.OAuthNotAuthorizedException;
import oauth.signpost.http.HttpRequest;

public class DefaultOAuthProvider implements OAuthProvider {

    private String requestTokenEndpointUrl;

    private String accessTokenEndpointUrl;

    private String authorizationWebsiteUrl;

    private OAuthConsumer consumer;

    private HttpURLConnection connection;

    private Map<String, String> responseParameters;

    public DefaultOAuthProvider(OAuthConsumer consumer,
            String requestTokenEndpointUrl, String accessTokenEndpointUrl,
            String authorizationWebsiteUrl) {
        this.consumer = consumer;
        this.requestTokenEndpointUrl = requestTokenEndpointUrl;
        this.accessTokenEndpointUrl = accessTokenEndpointUrl;
        this.authorizationWebsiteUrl = authorizationWebsiteUrl;
        this.responseParameters = new HashMap<String, String>();
    }

    public String retrieveRequestToken(String callbackUrl)
            throws OAuthMessageSignerException, OAuthNotAuthorizedException,
            OAuthExpectationFailedException, OAuthCommunicationException {

        // invalidate current credentials, if any
        consumer.setTokenWithSecret(null, null);

        // 1.0a expects the callback to be sent while getting the request token.
        // 1.0 service providers would simply ignore this parameter.
        retrieveToken(OAuth.addQueryParameters(requestTokenEndpointUrl,
                OAuth.OAUTH_CALLBACK, OAuth.percentEncode(callbackUrl)));

        String callbackConfirmed = responseParameters.get(OAuth.OAUTH_CALLBACK_CONFIRMED);
        responseParameters.remove(OAuth.OAUTH_CALLBACK_CONFIRMED);
        boolean isOAuth10a = Boolean.TRUE.toString().equals(callbackConfirmed);

        // 1.0 service providers expect the callback as part of the auth URL,
        // Do not send when 1.0a.
        if (isOAuth10a) {
            return OAuth.addQueryParameters(authorizationWebsiteUrl,
                    OAuth.OAUTH_TOKEN, OAuth.percentEncode(consumer.getToken()));
        } else {
            return OAuth.addQueryParameters(authorizationWebsiteUrl,
                    OAuth.OAUTH_TOKEN,
                    OAuth.percentEncode(consumer.getToken()),
                    OAuth.OAUTH_CALLBACK, OAuth.percentEncode(callbackUrl));
        }
    }

    public void retrieveAccessToken(String oauthVerifier)
            throws OAuthMessageSignerException, OAuthNotAuthorizedException,
            OAuthExpectationFailedException, OAuthCommunicationException {

        if (consumer.getToken() == null || consumer.getTokenSecret() == null) {
            throw new OAuthExpectationFailedException(
                    "Authorized request token or token secret not set. "
                            + "Did you retrieve an authorized request token before?");
        }

        retrieveToken(oauthVerifier == null
                ? accessTokenEndpointUrl
                : OAuth.addQueryParameters(accessTokenEndpointUrl,
                        OAuth.OAUTH_VERIFIER, oauthVerifier));
    }

    private void retrieveToken(String endpointUrl)
            throws OAuthMessageSignerException, OAuthCommunicationException,
            OAuthNotAuthorizedException, OAuthExpectationFailedException {

        if (consumer.getConsumerKey() == null
                || consumer.getConsumerSecret() == null) {
            throw new OAuthExpectationFailedException(
                    "Consumer key or secret not set");
        }

        try {
            if (connection == null) {
                connection = (HttpURLConnection) new URL(endpointUrl).openConnection();
                connection.setRequestMethod("GET");
            }
            HttpRequest request = new HttpRequestAdapter(connection);

            consumer.sign(request);

            connection.connect();

            int statusCode = connection.getResponseCode();

            if (statusCode == 401) {
                throw new OAuthNotAuthorizedException();
            }

            List<Parameter> params = OAuth.decodeForm(connection.getInputStream());
            responseParameters = OAuth.toMap(params);

            String token = responseParameters.get(OAuth.OAUTH_TOKEN);
            responseParameters.remove(OAuth.OAUTH_TOKEN);
            String secret = responseParameters.get(OAuth.OAUTH_TOKEN_SECRET);
            responseParameters.remove(OAuth.OAUTH_TOKEN_SECRET);

            if (token == null || secret == null) {
                throw new OAuthExpectationFailedException(
                        "Request token or token secret not set in server reply. "
                                + "The service provider you use is probably buggy.");
            }

            consumer.setTokenWithSecret(token, secret);

        } catch (OAuthNotAuthorizedException e) {
            throw e;
        } catch (OAuthExpectationFailedException e) {
            throw e;
        } catch (Exception e) {
            throw new OAuthCommunicationException(e);
        } finally {
            if (connection != null) {
                connection.disconnect();
                connection = null;
            }
        }
    }

    public Map<String, String> getResponseParameters() {
        return responseParameters;
    }

    void setHttpUrlConnection(HttpURLConnection connection) {
        this.connection = connection;
    }
}
