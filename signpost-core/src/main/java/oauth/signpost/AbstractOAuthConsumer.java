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
import java.util.Map;

import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;
import oauth.signpost.http.HttpRequest;
import oauth.signpost.http.RequestParameters;
import oauth.signpost.signature.HmacSha1MessageSigner;
import oauth.signpost.signature.OAuthMessageSigner;
import oauth.signpost.signature.SignatureBaseString;

public abstract class AbstractOAuthConsumer implements OAuthConsumer {

    private static final long serialVersionUID = 1L;

    private String consumerKey, consumerSecret;

	private String token;

	private OAuthMessageSigner messageSigner;

    private Map<String, String> oauthHeaderParams;

    private RequestParameters requestParameters;

    private boolean sendEmptyTokens;

    public AbstractOAuthConsumer(String consumerKey, String consumerSecret,
            OAuthMessageSigner messageSigner) {
        this.consumerKey = consumerKey;
        this.consumerSecret = consumerSecret;
        setMessageSigner(messageSigner);
    }

    public AbstractOAuthConsumer(String consumerKey, String consumerSecret) {
        this.consumerKey = consumerKey;
        this.consumerSecret = consumerSecret;
        setMessageSigner(new HmacSha1MessageSigner());
    }

    public void setMessageSigner(OAuthMessageSigner messageSigner) {
        this.messageSigner = messageSigner;
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

        requestParameters = new RequestParameters();
        try {
            collectHeaderParameters(request, requestParameters);
            collectQueryParameters(request, requestParameters);
            collectBodyParameters(request, requestParameters);

            // add any OAuth params that haven't already been set
            completeOAuthParameters(requestParameters);

            // remove any 'realm' and 'oauth_signature' params, as they must
            // not become part of the signature
            requestParameters.remove("realm");
            requestParameters.remove(OAuth.OAUTH_SIGNATURE);

        } catch (IOException e) {
            throw new OAuthCommunicationException(e);
        }

        String signature = messageSigner.sign(request, requestParameters);

        writeSignature(request, signature);

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

    /**
     * Helper method that adds any OAuth parameters to the given request
     * parameters which are missing from the current request but required for
     * signing. A good example is the oauth_nonce parameter, which is typically
     * not provided by the client in advance.
     * 
     * @param out
     *        the request parameter which should be completed
     */
    protected void completeOAuthParameters(RequestParameters out) {
        if (!oauthHeaderParams.containsKey(OAuth.OAUTH_CONSUMER_KEY)) {
            oauthHeaderParams.put(OAuth.OAUTH_CONSUMER_KEY, consumerKey);
        }
        if (!oauthHeaderParams.containsKey(OAuth.OAUTH_SIGNATURE_METHOD)) {
            oauthHeaderParams.put(OAuth.OAUTH_SIGNATURE_METHOD, messageSigner.getSignatureMethod());
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
            if (token != null && !token.equals("") || sendEmptyTokens) {
                oauthHeaderParams.put(OAuth.OAUTH_TOKEN, token);
            }
        }
        out.putMap(this.oauthHeaderParams);
    }

    public RequestParameters getRequestParameters() {
        return requestParameters;
    }

    /**
     * <p>
     * If you're seeing 401s during calls to
     * {@link OAuthProvider#retrieveRequestToken}, try setting this to true.
     * </p>
     * 
     * @param enable
     *        true or false
     */
    public void sendEmptyTokens(boolean enable) {
        this.sendEmptyTokens = enable;
    }

    /**
     * Collects OAuth Authorization header parameters as per OAuth Core 1.0 spec
     * section 9.1.1
     */
    protected void collectHeaderParameters(HttpRequest request, RequestParameters out) {
        this.oauthHeaderParams = OAuth.oauthHeaderToParamsMap(request
            .getHeader(OAuth.HTTP_AUTHORIZATION_HEADER));
    }

    /**
     * Collects x-www-form-urlencoded body parameters as per OAuth Core 1.0 spec
     * section 9.1.1
     */
    protected void collectBodyParameters(HttpRequest request, RequestParameters out)
            throws IOException {

        // collect x-www-form-urlencoded body params
        String contentType = request.getContentType();
        if (contentType != null && contentType.equals(OAuth.FORM_ENCODED)) {
            InputStream payload = request.getMessagePayload();
            out.putMap(OAuth.decodeForm(payload));
        }
    }

    /**
     * Collects HTTP GET query string parameters as per OAuth Core 1.0 spec
     * section 9.1.1
     */
    protected void collectQueryParameters(HttpRequest request, RequestParameters out) {

        String url = request.getRequestUrl();
        int q = url.indexOf('?');
        if (q >= 0) {
            // Combine the URL query string with the other parameters:
            out.putMap(OAuth.decodeForm(url.substring(q + 1)));
        }
    }

    /**
     * Helper method which constructs an OAuth authorization string that can be
     * placed in the HTTP Authorization header.
     * 
     * @param signature
     *        the message signature
     * @return the OAuth HTTP Authorization header value
     */
    protected String buildOAuthHeader(String signature) {

        StringBuilder sb = new StringBuilder();

        sb.append("OAuth ");

        for (String key : oauthHeaderParams.keySet()) {
            sb.append(oauthHeaderElement(key, oauthHeaderParams.get(key)));
            sb.append(",");
        }

        sb.append(oauthHeaderElement(OAuth.OAUTH_SIGNATURE, signature));

        return sb.toString();
    }

    /**
     * Helper method to concatenate an OAuth parameter and its value to a pair.
     * This method percent encodes both parts before joining them.
     * 
     * @param name
     *        the OAuth parameter name, e.g. oauth_token
     * @param value
     *        the OAuth parameter value, e.g. 'hello oauth'
     * @return a name/value pair, e.g. oauth_token='hello%20oauth'
     */
    protected String oauthHeaderElement(String name, String value) {
		return OAuth.percentEncode(name) + "=\"" + OAuth.percentEncode(value)
				+ "\"";
	}

    /**
     * Writes the signature to the given HTTP request. The default
     * implementation writes it to the HTTP Authorization header.
     * 
     * @param request
     *        the HTTP request to sign
     * @param signature
     *        the signature as computed by {@link SignatureBaseString}
     */
    protected void writeSignature(HttpRequest request, String signature) {
        request.setHeader(OAuth.HTTP_AUTHORIZATION_HEADER, buildOAuthHeader(signature));
    }
}
