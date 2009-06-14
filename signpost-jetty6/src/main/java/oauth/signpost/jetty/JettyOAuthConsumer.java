package oauth.signpost.jetty;

import oauth.signpost.AbstractOAuthConsumer;
import oauth.signpost.http.HttpRequest;
import oauth.signpost.signature.SignatureMethod;

import org.mortbay.jetty.client.HttpExchange;

public class JettyOAuthConsumer extends AbstractOAuthConsumer {

    public JettyOAuthConsumer(String consumerKey, String consumerSecret,
            SignatureMethod signatureMethod) {
        super(consumerKey, consumerSecret, signatureMethod);
    }

    @Override
    protected HttpRequest wrap(Object request) {
        return new HttpRequestAdapter((HttpExchange) request);
    }

}
