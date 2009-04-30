package oauth.signpost;

import oauth.signpost.exception.OAuthMessageSignerException;

import org.apache.http.HttpRequest;

public interface OAuthConsumer {

    public HttpRequest sign(HttpRequest request)
            throws OAuthMessageSignerException;

    public void setTokenWithSecret(String token, String tokenSecret);

    public String getToken();

    public String getTokenSecret();

    public String getConsumerKey();

    public String getConsumerSecret();
}
