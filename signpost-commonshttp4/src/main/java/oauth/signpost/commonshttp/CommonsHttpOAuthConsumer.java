package oauth.signpost.commonshttp;

import oauth.signpost.AbstractOAuthConsumer;
import oauth.signpost.http.HttpRequest;
import oauth.signpost.signature.SignatureMethod;

@SuppressWarnings("serial")
public class CommonsHttpOAuthConsumer extends AbstractOAuthConsumer {

    public CommonsHttpOAuthConsumer(String consumerKey, String consumerSecret,
            SignatureMethod signatureMethod) {
        super(consumerKey, consumerSecret, signatureMethod);
    }

    @Override
    protected HttpRequest wrap(Object request) {
        if (!(request instanceof org.apache.http.HttpRequest)) {
            throw new IllegalArgumentException(
                    "This consumer expects requests of type "
                            + org.apache.http.HttpRequest.class.getCanonicalName());
        }

        return new HttpRequestAdapter((org.apache.http.client.methods.HttpUriRequest) request);
    }

}
