package oauth.signpost.signature;

import java.util.Map;

import oauth.signpost.exception.OAuthMessageSignerException;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpRequest;

public abstract class OAuthMessageSigner {

    private Base64 base64;

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
}
