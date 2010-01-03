package oauth.signpost.jetty;

import oauth.signpost.AbstractOAuthConsumer;
import oauth.signpost.http.HttpRequest;
import oauth.signpost.signature.OAuthMessageSigner;

import org.mortbay.jetty.client.HttpExchange;

@SuppressWarnings("serial")
public class JettyOAuthConsumer extends AbstractOAuthConsumer {

    public JettyOAuthConsumer(String consumerKey, String consumerSecret,
            OAuthMessageSigner messageSigner) {
        super(consumerKey, consumerSecret, messageSigner);
    }

    @Override
    protected HttpRequest wrap(Object request) {
        return new HttpRequestAdapter((HttpExchange) request);
    }

}
