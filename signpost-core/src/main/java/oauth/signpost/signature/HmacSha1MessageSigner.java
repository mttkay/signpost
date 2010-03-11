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

import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import oauth.signpost.OAuth;
import oauth.signpost.exception.OAuthMessageSignerException;
import oauth.signpost.http.HttpRequest;
import oauth.signpost.http.HttpParameters;

@SuppressWarnings("serial")
public class HmacSha1MessageSigner extends OAuthMessageSigner {

	private static final String MAC_NAME = "HmacSHA1";

	@Override
    public String getSignatureMethod() {
        return "HMAC-SHA1";
    }

    @Override
    public String sign(HttpRequest request, HttpParameters requestParams)
            throws OAuthMessageSignerException {
        try {
            String keyString = OAuth.percentEncode(getConsumerSecret()) + '&'
                    + OAuth.percentEncode(getTokenSecret());
            byte[] keyBytes = keyString.getBytes(OAuth.ENCODING);

            SecretKey key = new SecretKeySpec(keyBytes, MAC_NAME);
            Mac mac = Mac.getInstance(MAC_NAME);
            mac.init(key);

            String sbs = new SignatureBaseString(request, requestParams).generate();
            OAuth.debugOut("SBS", sbs);
            byte[] text = sbs.getBytes(OAuth.ENCODING);

            return base64Encode(mac.doFinal(text)).trim();
        } catch (GeneralSecurityException e) {
            throw new OAuthMessageSignerException(e);
        } catch (UnsupportedEncodingException e) {
            throw new OAuthMessageSignerException(e);
        }
    }
}
