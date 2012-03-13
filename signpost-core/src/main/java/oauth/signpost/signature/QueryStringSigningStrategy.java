package oauth.signpost.signature;

import java.util.Iterator;

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

        // add all (x_)oauth parameters
        HttpParameters oauthParams = requestParameters.getOAuthParameters();
        oauthParams.put(OAuth.OAUTH_SIGNATURE, signature, true);

        Iterator<String> iter = oauthParams.keySet().iterator();

        // add the first query parameter (we always have at least the signature)
        String firstKey = iter.next();
        StringBuilder sb = new StringBuilder(OAuth.addQueryString(request.getRequestUrl(),
            oauthParams.getAsQueryString(firstKey)));

        while (iter.hasNext()) {
            sb.append("&");
            String key = iter.next();
            sb.append(oauthParams.getAsQueryString(key));
        }

        String signedUrl = sb.toString();

        request.setRequestUrl(signedUrl);

        return signedUrl;
    }

}
