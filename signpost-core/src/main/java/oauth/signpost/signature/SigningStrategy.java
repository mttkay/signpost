package oauth.signpost.signature;

import java.io.Serializable;

import oauth.signpost.http.HttpRequest;
import oauth.signpost.http.HttpParameters;

/**
 * <p>
 * Defines how an OAuth signature string is written to a request.
 * </p>
 * <p>
 * Unlike {@link OAuthMessageSigner}, which is concerned with <i>how</i> to
 * generate a signature, this class is concered with <i>where</i> to write it
 * (e.g. HTTP header or query string).
 * </p>
 * 
 * @author Matthias Kaeppler
 */
public interface SigningStrategy extends Serializable {

    /**
     * Writes an OAuth signature and all remaining required parameters to an
     * HTTP message.
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
    String writeSignature(String signature, HttpRequest request, HttpParameters requestParameters);
    
}
