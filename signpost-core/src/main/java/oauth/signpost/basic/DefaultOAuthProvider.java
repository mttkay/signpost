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
import java.util.Map;

import oauth.signpost.AbstractOAuthProvider;
import oauth.signpost.OAuth;
import oauth.signpost.OAuthConsumer;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;
import oauth.signpost.exception.OAuthNotAuthorizedException;
import oauth.signpost.http.HttpRequest;

/**
 * This default implementation uses {@link java.net.HttpURLConnection} type GET
 * requests to receive tokens from a service provider.
 * 
 * @author Matthias Kaeppler
 */
public class DefaultOAuthProvider extends AbstractOAuthProvider {

    private static final long serialVersionUID = 1L;

    private HttpURLConnection connection;

    public DefaultOAuthProvider(String requestTokenEndpointUrl, String accessTokenEndpointUrl,
            String authorizationWebsiteUrl) {
        super(requestTokenEndpointUrl, accessTokenEndpointUrl, authorizationWebsiteUrl);
    }

    protected void retrieveToken(OAuthConsumer consumer, String endpointUrl)
            throws OAuthMessageSignerException, OAuthCommunicationException,
            OAuthNotAuthorizedException, OAuthExpectationFailedException {

        Map<String, String> defaultHeaders = getRequestHeaders();

        if (consumer.getConsumerKey() == null || consumer.getConsumerSecret() == null) {
            throw new OAuthExpectationFailedException("Consumer key or secret not set");
        }

        try {
            if (connection == null) {
                connection = (HttpURLConnection) new URL(endpointUrl).openConnection();
                connection.setRequestMethod("GET");
            }
            HttpRequest request = new HttpURLConnectionRequestAdapter(connection);
            for (String header : defaultHeaders.keySet()) {
                request.setHeader(header, defaultHeaders.get(header));
            }

            consumer.sign(request);

            connection.connect();

            int statusCode = connection.getResponseCode();

            if (statusCode == 401) {
                throw new OAuthNotAuthorizedException();
            }

            Map<String, String> responseParams = OAuth.decodeForm(connection.getInputStream());

            String token = responseParams.get(OAuth.OAUTH_TOKEN);
            String secret = responseParams.get(OAuth.OAUTH_TOKEN_SECRET);
            responseParams.remove(OAuth.OAUTH_TOKEN);
            responseParams.remove(OAuth.OAUTH_TOKEN_SECRET);

            setResponseParameters(responseParams);

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

    void setHttpUrlConnection(HttpURLConnection connection) {
        this.connection = connection;
    }
}
