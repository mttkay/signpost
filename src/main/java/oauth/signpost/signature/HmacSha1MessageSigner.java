package oauth.signpost.signature;

import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.util.Map;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import oauth.signpost.OAuth;
import oauth.signpost.exception.OAuthMessageSignerException;

import org.apache.http.HttpRequest;

public class HmacSha1MessageSigner extends OAuthMessageSigner {

    private static final String MAC_NAME = "HmacSHA1";

    @Override
    protected String sign(HttpRequest request,
            Map<String, String> oauthParameters)
            throws OAuthMessageSignerException {
        try {
            String keyString = OAuth.percentEncode(getConsumerSecret()) + '&'
                    + OAuth.percentEncode(getTokenSecret());
            byte[] keyBytes = keyString.getBytes(OAuth.ENCODING);

            SecretKey key = new SecretKeySpec(keyBytes, MAC_NAME);
            Mac mac = Mac.getInstance(MAC_NAME);
            mac.init(key);

            String sbs = computeSignatureBaseString(request, oauthParameters);
            byte[] text = sbs.getBytes(OAuth.ENCODING);

            return base64Encode(mac.doFinal(text));
        } catch (GeneralSecurityException e) {
            throw new OAuthMessageSignerException(e);
        } catch (UnsupportedEncodingException e) {
            throw new OAuthMessageSignerException(e);
        }
    }
}
