package oauth.signpost.signature;

import oauth.signpost.OAuth;
import oauth.signpost.exception.OAuthMessageSignerException;
import oauth.signpost.http.HttpParameters;
import oauth.signpost.http.HttpRequest;

import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;

@SuppressWarnings("serial")
public class RsaSha1MessageSigner extends OAuthMessageSigner {

    @Override
    public String getSignatureMethod() {
        return "RSA-SHA1";
    }

    @Override
    public String sign(HttpRequest request, HttpParameters requestParameters) throws OAuthMessageSignerException {
        byte[] privateKeyBytes = decodeBase64(getConsumerSecret());
        PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(privateKeyBytes);

        try {
            PrivateKey privateKey = KeyFactory.getInstance("RSA").generatePrivate(privateKeySpec);
            Signature signer = Signature.getInstance("SHA1withRSA");

            String sbs = new SignatureBaseString(request, requestParameters).generate();
            OAuth.debugOut("SBS", sbs);
            byte[] text = sbs.getBytes(OAuth.ENCODING);

            signer.initSign(privateKey);
            signer.update(text);

            return base64Encode(signer.sign());
        } catch (GeneralSecurityException e) {
            throw new OAuthMessageSignerException(e);
        } catch (UnsupportedEncodingException e) {
            throw new OAuthMessageSignerException(e);
        }
    }

}
