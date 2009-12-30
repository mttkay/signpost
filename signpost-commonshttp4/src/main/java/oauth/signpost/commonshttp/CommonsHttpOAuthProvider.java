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
package oauth.signpost.commonshttp;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import oauth.signpost.AbstractOAuthProvider;
import oauth.signpost.OAuth;
import oauth.signpost.OAuthConsumer;
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

/**
 * This implementation uses the {@link HttpClient} interface to fetch OAuth
 * tokens from a service provider.
 * 
 * @author Matthias Kaeppler
 */
@SuppressWarnings("serial")
public class CommonsHttpOAuthProvider extends AbstractOAuthProvider {

    private HttpClient httpClient;

    public CommonsHttpOAuthProvider(OAuthConsumer consumer,
            String requestTokenEndpointUrl, String accessTokenEndpointUrl,
            String authorizationWebsiteUrl) {
        super(consumer, requestTokenEndpointUrl, accessTokenEndpointUrl, authorizationWebsiteUrl);
        httpClient = new DefaultHttpClient();
    }
    
	protected void retrieveToken(String endpointUrl)
			throws OAuthMessageSignerException, OAuthCommunicationException,
			OAuthNotAuthorizedException, OAuthExpectationFailedException {

        OAuthConsumer consumer = getConsumer();
        Map<String, String> defaultHeaders = getRequestHeaders();

		if (consumer.getConsumerKey() == null
				|| consumer.getConsumerSecret() == null) {
			throw new OAuthExpectationFailedException(
					"Consumer key or secret not set");
		}

        HttpGet request = new HttpGet(endpointUrl);
        for (String header : defaultHeaders.keySet()) {
            request.setHeader(header, defaultHeaders.get(header));
        }
        HttpResponse response = null;

		try {

            consumer.sign(new HttpRequestAdapter(request));

            response = httpClient.execute(request);

			int statusCode = response.getStatusLine().getStatusCode();

			if (statusCode == 401) {
				throw new OAuthNotAuthorizedException();
			}

            List<Parameter> params = OAuth.decodeForm(response.getEntity().getContent());
            Map<String, String> responseParams = OAuth.toMap(params);

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
            if (response != null) {
                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    try {
                        // free the connection
                        entity.consumeContent();
                    } catch (IOException e) {
                        // this means HTTP keep-alive is not possible
                        e.printStackTrace();
                    }
                }
            }
		}
	}

    void setHttpClient(HttpClient httpClient) {
        this.httpClient = httpClient;
    }
}
