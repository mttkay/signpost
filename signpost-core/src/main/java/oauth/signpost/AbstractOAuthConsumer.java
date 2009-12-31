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

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;
import oauth.signpost.http.HttpRequest;
import oauth.signpost.signature.OAuthMessageSigner;
import oauth.signpost.signature.SignatureMethod;

@SuppressWarnings("serial")
public abstract class AbstractOAuthConsumer implements OAuthConsumer {

	private String consumerKey, consumerSecret;

	private String token;

	private SignatureMethod signatureMethod;

	private OAuthMessageSigner messageSigner;

    private Map<String, String> requestParameters, oauthHeaderParams;

	public AbstractOAuthConsumer(String consumerKey, String consumerSecret,
			SignatureMethod signatureMethod) {

		this.consumerKey = consumerKey;
		this.consumerSecret = consumerSecret;
		this.signatureMethod = signatureMethod;
		this.messageSigner = OAuthMessageSigner.create(signatureMethod);
		messageSigner.setConsumerSecret(consumerSecret);
	}

    public HttpRequest sign(HttpRequest request) throws OAuthMessageSignerException,
            OAuthExpectationFailedException, OAuthCommunicationException {
        if (consumerKey == null) {
            throw new OAuthExpectationFailedException("consumer key not set");
        }
        if (consumerSecret == null) {
            throw new OAuthExpectationFailedException("consumer secret not set");
        }

        this.requestParameters = new HashMap<String, String>();
        try {
            collectHeaderParameters(request);
            collectQueryParameters(request);
            collectBodyParameters(request);

            // add any OAuth params that haven't already been set
            completeOAuthParameters();
        } catch (IOException e) {
            throw new OAuthCommunicationException(e);
        }

        String signature = messageSigner.sign(request, requestParameters);

        request.setHeader(OAuth.HTTP_AUTHORIZATION_HEADER, buildOAuthHeader(signature));

        return request;
    }

	public HttpRequest sign(Object request) throws OAuthMessageSignerException,
            OAuthExpectationFailedException, OAuthCommunicationException {
		return sign(wrap(request));
	}

	protected abstract HttpRequest wrap(Object request);

	public void setTokenWithSecret(String token, String tokenSecret) {
		this.token = token;
		messageSigner.setTokenSecret(tokenSecret);
	}

	public String getToken() {
		return token;
	}

	public String getTokenSecret() {
		return messageSigner.getTokenSecret();
	}

	public String getConsumerKey() {
		return this.consumerKey;
	}

	public String getConsumerSecret() {
		return this.consumerSecret;
	}

    private void completeOAuthParameters() {
        if (!oauthHeaderParams.containsKey(OAuth.OAUTH_CONSUMER_KEY)) {
            oauthHeaderParams.put(OAuth.OAUTH_CONSUMER_KEY, consumerKey);
        }
        if (!oauthHeaderParams.containsKey(OAuth.OAUTH_SIGNATURE_METHOD)) {
            oauthHeaderParams.put(OAuth.OAUTH_SIGNATURE_METHOD, signatureMethod.toString());
        }
        if (!oauthHeaderParams.containsKey(OAuth.OAUTH_TIMESTAMP)) {
            oauthHeaderParams.put(OAuth.OAUTH_TIMESTAMP, Long
                .toString(System.currentTimeMillis() / 1000L));
        }
        if (!oauthHeaderParams.containsKey(OAuth.OAUTH_NONCE)) {
            oauthHeaderParams.put(OAuth.OAUTH_NONCE, Long.toString(System.nanoTime()));
        }
        if (!oauthHeaderParams.containsKey(OAuth.OAUTH_VERSION)) {
            oauthHeaderParams.put(OAuth.OAUTH_VERSION, OAuth.VERSION_1_0);
        }
        if (!oauthHeaderParams.containsKey(OAuth.OAUTH_TOKEN)) {
            oauthHeaderParams.put(OAuth.OAUTH_TOKEN, token);
        }
        this.requestParameters.putAll(this.oauthHeaderParams);
    }

    /**
     * Collects OAuth Authorization header parameters as per OAuth Core 1.0 spec
     * section 9.1.1
     */
    private void collectHeaderParameters(HttpRequest request) {
        this.oauthHeaderParams = OAuth.oauthHeaderToParamsMap(request
            .getHeader(OAuth.HTTP_AUTHORIZATION_HEADER));
    }

    /**
     * Collects x-www-form-urlencoded body parameters as per OAuth Core 1.0 spec
     * section 9.1.1
     */
    private void collectBodyParameters(HttpRequest request) throws IOException {

        // collect x-www-form-urlencoded body params
        String contentType = request.getContentType();
        if (contentType != null && contentType.equals(OAuth.FORM_ENCODED)) {
            InputStream payload = request.getMessagePayload();
            this.requestParameters.putAll(OAuth.decodeForm(payload));
        }
    }

    /**
     * Collects HTTP GET query string parameters as per OAuth Core 1.0 spec
     * section 9.1.1
     */
    private void collectQueryParameters(HttpRequest request) {

        String url = request.getRequestUrl();
        int q = url.indexOf('?');
        if (q >= 0) {
            // Combine the URL query string with the other parameters:
            this.requestParameters.putAll(OAuth.decodeForm(url.substring(q + 1)));
        }
    }

    private String buildOAuthHeader(String signature) {

        StringBuilder sb = new StringBuilder();

        sb.append("OAuth ");

        for (String key : oauthHeaderParams.keySet()) {
            sb.append(oauthHeaderElement(key, oauthHeaderParams.get(key)));
            sb.append(",");
        }

        sb.append(oauthHeaderElement(OAuth.OAUTH_SIGNATURE, signature));

        return sb.toString();
    }

	private String oauthHeaderElement(String name, String value) {
		return OAuth.percentEncode(name) + "=\"" + OAuth.percentEncode(value)
				+ "\"";
	}
}
