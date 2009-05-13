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
package oauth.signpost.impl;

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

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

public class DefaultOAuthProvider implements OAuthProvider {

    private String requestTokenEndpointUrl;

    private String accessTokenEndpointUrl;

    private String authorizationWebsiteUrl;

    private OAuthConsumer consumer;

    private HttpClient httpClient;

    public DefaultOAuthProvider(OAuthConsumer consumer,
            String requestTokenEndpointUrl, String accessTokenEndpointUrl,
            String authorizationWebsiteUrl) {
        this.consumer = consumer;
        this.requestTokenEndpointUrl = requestTokenEndpointUrl;
        this.accessTokenEndpointUrl = accessTokenEndpointUrl;
        this.authorizationWebsiteUrl = authorizationWebsiteUrl;
    }

    public void setHttpClient(HttpClient httpClient) {
        this.httpClient = httpClient;
    }

    public String retrieveRequestToken(String callbackUrl)
            throws OAuthMessageSignerException, OAuthNotAuthorizedException,
            OAuthExpectationFailedException, OAuthCommunicationException {

        // invalidate current credentials, if any
        consumer.setTokenWithSecret(null, null);

        retrieveToken(requestTokenEndpointUrl);

        return authorizationWebsiteUrl + "?oauth_token="
                + OAuth.percentEncode(consumer.getToken()) + "&"
                + OAuth.OAUTH_CALLBACK + "=" + OAuth.percentEncode(callbackUrl);
    }

    public void retrieveAccessToken() throws OAuthMessageSignerException,
            OAuthNotAuthorizedException, OAuthExpectationFailedException,
            OAuthCommunicationException {

        if (consumer.getToken() == null || consumer.getTokenSecret() == null) {
            throw new OAuthExpectationFailedException(
                    "Authorized request token or token secret not set. "
                            + "Did you retrieve an authorized request token before?");
        }

        retrieveToken(accessTokenEndpointUrl + "?" + OAuth.OAUTH_TOKEN + "="
                + consumer.getToken() + "&" + OAuth.OAUTH_TOKEN_SECRET + "="
                + consumer.getTokenSecret());
    }

    private void retrieveToken(String endpointUrl)
            throws OAuthMessageSignerException, OAuthCommunicationException,
            OAuthNotAuthorizedException, OAuthExpectationFailedException {

        if (consumer.getConsumerKey() == null
                || consumer.getConsumerSecret() == null) {
            throw new OAuthExpectationFailedException(
                    "Consumer key or secret not set");
        }

        if (httpClient == null) {
            httpClient = new DefaultHttpClient();
        }
        HttpGet request = new HttpGet(endpointUrl);

        consumer.sign(request);

        try {
            HttpResponse response = httpClient.execute(request);
            int statusCode = response.getStatusLine().getStatusCode();

            if (statusCode == 401) {
                throw new OAuthNotAuthorizedException();
            }

            HttpEntity entity = response.getEntity();
            List<Parameter> params = OAuth.decodeForm(entity.getContent());
            Map<String, String> paramMap = OAuth.toMap(params);

            String token = paramMap.get(OAuth.OAUTH_TOKEN);
            String secret = paramMap.get(OAuth.OAUTH_TOKEN_SECRET);

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
        }
    }

}
