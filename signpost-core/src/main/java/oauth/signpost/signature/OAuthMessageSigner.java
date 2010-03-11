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

import oauth.signpost.exception.OAuthMessageSignerException;
import oauth.signpost.http.HttpRequest;
import oauth.signpost.http.HttpParameters;

import org.apache.commons.codec.binary.Base64;

public abstract class OAuthMessageSigner implements Serializable {

    private static final long serialVersionUID = 4445779788786131202L;

    private transient Base64 base64;

    private String consumerSecret;

    private String tokenSecret;

    public OAuthMessageSigner() {
        this.base64 = new Base64();
    }

    public abstract String sign(HttpRequest request, HttpParameters requestParameters)
            throws OAuthMessageSignerException;

    public abstract String getSignatureMethod();

    public String getConsumerSecret() {
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

    private void readObject(java.io.ObjectInputStream stream)
            throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        this.base64 = new Base64();
    }
}
