package oauth.signpost.basic;

import java.net.HttpURLConnection;

import oauth.signpost.AbstractOAuthConsumer;
import oauth.signpost.http.HttpRequest;
import oauth.signpost.signature.SignatureMethod;

public class DefaultOAuthConsumer extends AbstractOAuthConsumer {

    public DefaultOAuthConsumer(String consumerKey, String consumerSecret,
            SignatureMethod signatureMethod) {
        super(consumerKey, consumerSecret, signatureMethod);
    }

    @Override
    protected HttpRequest wrap(Object request) {
        if (!(request instanceof HttpURLConnection)) {
            throw new IllegalArgumentException(
                    "The default consumer expects requests of type java.net.HttpURLConnection");
        }
        return new HttpRequestAdapter((HttpURLConnection) request);
    }

}
