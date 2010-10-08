package oauth.signpost.appengine;

import oauth.signpost.AbstractOAuthConsumer;
import oauth.signpost.http.HttpRequest;

import com.google.appengine.api.urlfetch.HTTPRequest;

/**
 * Supports signing HTTP requests of type
 * {@link com.google.appengine.api.urlfetch.HTTPRequest}
 * 
 * @author hleinone
 */
public class GoogleAppEngineOAuthConsumer extends AbstractOAuthConsumer {
	public GoogleAppEngineOAuthConsumer(String consumerKey,
			String consumerSecret) {
		super(consumerKey, consumerSecret);
	}

	private static final long serialVersionUID = 8714138553645444690L;

	@Override
	protected HttpRequest wrap(Object request) {
		if (!(request instanceof HTTPRequest)) {
			throw new IllegalArgumentException(
					"This consumer expects requests of type "
							+ HTTPRequest.class.getCanonicalName());
		}

		return new HttpRequestAdapter((HTTPRequest) request);
	}
}
