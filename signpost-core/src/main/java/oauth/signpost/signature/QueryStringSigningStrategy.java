package oauth.signpost.signature;

import oauth.signpost.OAuth;
import oauth.signpost.http.HttpRequest;
import oauth.signpost.http.RequestParameters;

public class QueryStringSigningStrategy implements SigningStrategy {

    private static final long serialVersionUID = 1L;

    public String writeSignature(String signature, HttpRequest request,
            RequestParameters requestParameters) {

        // add the signature
        StringBuilder sb = new StringBuilder(OAuth.addQueryParameters(request.getRequestUrl(),
            OAuth.OAUTH_SIGNATURE, signature));

        // add the optional OAuth parameters
        if (requestParameters.containsKey(OAuth.OAUTH_TOKEN)) {
            sb.append("&");
            sb.append(requestParameters.getFormEncoded(OAuth.OAUTH_TOKEN));
        }
        if (requestParameters.containsKey(OAuth.OAUTH_CALLBACK)) {
            sb.append("&");
            sb.append(requestParameters.getFormEncoded(OAuth.OAUTH_CALLBACK));
        }

        // add the remaining OAuth params
        sb.append("&");
        sb.append(requestParameters.getFormEncoded(OAuth.OAUTH_CONSUMER_KEY));
        sb.append("&");
        sb.append(requestParameters.getFormEncoded(OAuth.OAUTH_VERSION));
        sb.append("&");
        sb.append(requestParameters.getFormEncoded(OAuth.OAUTH_SIGNATURE_METHOD));
        sb.append("&");
        sb.append(requestParameters.getFormEncoded(OAuth.OAUTH_TIMESTAMP));
        sb.append("&");
        sb.append(requestParameters.getFormEncoded(OAuth.OAUTH_NONCE));

        String signedUrl = sb.toString();

        request.setRequestUrl(signedUrl);

        return signedUrl;
    }

}
