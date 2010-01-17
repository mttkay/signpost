package oauth.signpost.signature;

import java.io.Serializable;

import oauth.signpost.http.HttpRequest;
import oauth.signpost.http.RequestParameters;

/**
 * Unlike {@link OAuthMessageSigner}, which is concerned with how to generate a
 * signature, this class is concered with where to write it (e.g. HTTP header or
 * query string)
 * 
 * @author Matthias Kaeppler
 */
public interface SigningStrategy extends Serializable {

    /**
     * Writes a signature to an HTTP message.
     * 
     * @param signature
     *        the signature to write
     * @param request
     *        the request to sign
     * @param requestParameters
     *        the request parameters
     * @return whatever has been written to the request, e.g. an Authorization
     *         header field
     */
    String writeSignature(String signature, HttpRequest request, RequestParameters requestParameters);
    
}
