package oauth.signpost.signature;

import java.util.Map;

import oauth.signpost.OAuth;
import oauth.signpost.exception.OAuthMessageSignerException;

import org.apache.http.HttpRequest;

public class PlainTextMessageSigner extends OAuthMessageSigner {

    @Override
    public String sign(HttpRequest request, Map<String, String> oauthParameters)
            throws OAuthMessageSignerException {
        return OAuth.percentEncode(getConsumerSecret()) + '&'
                + OAuth.percentEncode(getTokenSecret());
    }
}
