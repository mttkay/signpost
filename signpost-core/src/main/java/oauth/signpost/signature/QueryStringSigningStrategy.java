package oauth.signpost.signature;

import oauth.signpost.OAuth;
import oauth.signpost.http.HttpParameters;
import oauth.signpost.http.HttpRequest;

/**
 * Writes to a URL query string. <strong>Note that this currently ONLY works
 * when signing a URL directly, not with HTTP request objects.</strong> That's
 * because most HTTP request implementations do not allow the client to change
 * the URL once the request has been instantiated, so there is no way to append
 * parameters to it.
 * 
 * @author Matthias Kaeppler
 */
public class QueryStringSigningStrategy implements SigningStrategy {

    private static final long serialVersionUID = 1L;

    public String writeSignature(String signature, HttpRequest request,
            HttpParameters requestParameters) {

        // add the signature
        StringBuilder sb = new StringBuilder(OAuth.addQueryParameters(request.getRequestUrl(),
            OAuth.OAUTH_SIGNATURE, signature));

        // add the optional OAuth parameters
        if (requestParameters.containsKey(OAuth.OAUTH_TOKEN)) {
            sb.append("&");
            sb.append(requestParameters.getAsQueryString(OAuth.OAUTH_TOKEN));
        }
        if (requestParameters.containsKey(OAuth.OAUTH_CALLBACK)) {
            sb.append("&");
            sb.append(requestParameters.getAsQueryString(OAuth.OAUTH_CALLBACK));
        }
        if (requestParameters.containsKey(OAuth.OAUTH_VERIFIER)) {
            sb.append("&");
            sb.append(requestParameters.getAsQueryString(OAuth.OAUTH_VERIFIER));
        }

        // add the remaining OAuth params
        sb.append("&");
        sb.append(requestParameters.getAsQueryString(OAuth.OAUTH_CONSUMER_KEY));
        sb.append("&");
        sb.append(requestParameters.getAsQueryString(OAuth.OAUTH_VERSION));
        sb.append("&");
        sb.append(requestParameters.getAsQueryString(OAuth.OAUTH_SIGNATURE_METHOD));
        sb.append("&");
        sb.append(requestParameters.getAsQueryString(OAuth.OAUTH_TIMESTAMP));
        sb.append("&");
        sb.append(requestParameters.getAsQueryString(OAuth.OAUTH_NONCE));

        String signedUrl = sb.toString();

        request.setRequestUrl(signedUrl);

        return signedUrl;
    }

}
