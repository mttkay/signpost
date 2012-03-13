package oauth.signpost.signature;

import java.util.Iterator;

import oauth.signpost.OAuth;
import oauth.signpost.http.HttpParameters;
import oauth.signpost.http.HttpRequest;

/**
 * Writes to the HTTP Authorization header field.
 * 
 * @author Matthias Kaeppler
 */
public class AuthorizationHeaderSigningStrategy implements SigningStrategy {

    private static final long serialVersionUID = 1L;

    public String writeSignature(String signature, HttpRequest request,
            HttpParameters requestParameters) {
        StringBuilder sb = new StringBuilder();

        sb.append("OAuth ");

        // add the realm parameter, if any
        if (requestParameters.containsKey("realm")) {
            sb.append(requestParameters.getAsHeaderElement("realm"));
            sb.append(", ");
        }

        // add all (x_)oauth parameters
        HttpParameters oauthParams = requestParameters.getOAuthParameters();
        oauthParams.put(OAuth.OAUTH_SIGNATURE, signature, true);

        Iterator<String> iter = oauthParams.keySet().iterator();
        while (iter.hasNext()) {
            String key = iter.next();
            sb.append(oauthParams.getAsHeaderElement(key));
            if (iter.hasNext()) {
                sb.append(", ");
            }
        }

        String header = sb.toString();
        OAuth.debugOut("Auth Header", header);
        request.setHeader(OAuth.HTTP_AUTHORIZATION_HEADER, header);

        return header;
    }

}
