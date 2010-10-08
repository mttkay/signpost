package oauth.signpost.appengine;

import java.net.URL;

import oauth.signpost.AbstractOAuthProvider;
import oauth.signpost.http.HttpRequest;
import oauth.signpost.http.HttpResponse;

import com.google.appengine.api.urlfetch.HTTPMethod;
import com.google.appengine.api.urlfetch.HTTPRequest;
import com.google.appengine.api.urlfetch.HTTPResponse;
import com.google.appengine.api.urlfetch.URLFetchServiceFactory;

/**
 * This implementation uses the Google App Engine
 * {@link com.google.appengine.api.urlfetch.URLFetchService} to fetch OAuth
 * tokens from a service provider.
 * 
 * @author hleinone
 */
public class GoogleAppEngineOAuthProvider extends AbstractOAuthProvider {

	private static final long serialVersionUID = 1L;

	public GoogleAppEngineOAuthProvider(String requestTokenEndpointUrl,
			String accessTokenEndpointUrl, String authorizationWebsiteUrl) {
		super(requestTokenEndpointUrl, accessTokenEndpointUrl,
				authorizationWebsiteUrl);
	}

	@Override
	protected HttpRequest createRequest(String endpointUrl) throws Exception {
		HTTPRequest request = new HTTPRequest(new URL(endpointUrl),
				HTTPMethod.POST);
		return new HttpRequestAdapter(request);
	}

	@Override
	protected HttpResponse sendRequest(HttpRequest request) throws Exception {
		HTTPResponse response = URLFetchServiceFactory.getURLFetchService()
				.fetch((HTTPRequest) request.unwrap());
		return new HttpResponseAdapter(response);
	}

	@Override
	protected void closeConnection(HttpRequest request, HttpResponse response)
			throws Exception {
	}
}
