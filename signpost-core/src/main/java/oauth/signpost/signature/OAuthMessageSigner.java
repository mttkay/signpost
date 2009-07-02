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
package oauth.signpost.signature;

import java.io.IOException;
import java.io.Serializable;
import java.util.Map;

import oauth.signpost.exception.OAuthMessageSignerException;
import oauth.signpost.http.HttpRequest;

import org.apache.commons.codec.binary.Base64;

public abstract class OAuthMessageSigner implements Serializable {

    private transient Base64 base64;

    private String consumerSecret;

    private String tokenSecret;

    public static OAuthMessageSigner create(SignatureMethod signatureMethod) {

        switch (signatureMethod) {

        case PLAINTEXT:
            return new PlainTextMessageSigner();

        case HMAC_SHA1:
            return new HmacSha1MessageSigner();
        }

        return null;
    }

    public OAuthMessageSigner() {
        this.base64 = new Base64();
    }

    public abstract String sign(HttpRequest request,
            Map<String, String> oauthParameters)
            throws OAuthMessageSignerException;

    protected String getConsumerSecret() {
        return consumerSecret;
    }

    public String getTokenSecret() {
        return tokenSecret;
    }

    public void setConsumerSecret(String consumerSecret) {
        this.consumerSecret = consumerSecret;
    }

    public void setTokenSecret(String tokenSecret) {
        this.tokenSecret = tokenSecret;
    }

    protected byte[] decodeBase64(String s) {
        return base64.decode(s.getBytes());
    }

    protected String base64Encode(byte[] b) {
        return new String(base64.encode(b));
    }

    protected String computeSignatureBaseString(HttpRequest request,
            Map<String, String> oauthParameters)
            throws OAuthMessageSignerException {
        SignatureBaseString sbs = new SignatureBaseString(request,
                oauthParameters);
        return sbs.compute();
    }

    private void readObject(java.io.ObjectInputStream stream)
            throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        this.base64 = new Base64();
    }
}
