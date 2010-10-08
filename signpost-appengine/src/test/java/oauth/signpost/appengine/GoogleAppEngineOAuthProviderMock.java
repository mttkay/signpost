package oauth.signpost.appengine;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import oauth.signpost.http.HttpRequest;
import oauth.signpost.mocks.OAuthProviderMock;

import org.mockito.Mockito;

import com.google.appengine.api.urlfetch.HTTPRequest;
import com.google.appengine.api.urlfetch.HTTPResponse;
import com.google.appengine.api.urlfetch.URLFetchService;

@SuppressWarnings("serial")
public class GoogleAppEngineOAuthProviderMock extends
		GoogleAppEngineOAuthProvider implements OAuthProviderMock {

	private URLFetchService urlFetchServiceMock;

	public GoogleAppEngineOAuthProviderMock(String requestTokenUrl,
			String accessTokenUrl, String websiteUrl) {
		super(requestTokenUrl, accessTokenUrl, websiteUrl);
	}

	@Override
	protected oauth.signpost.http.HttpResponse sendRequest(HttpRequest request)
			throws Exception {
		HTTPResponse resp = urlFetchServiceMock.fetch((HTTPRequest) request
				.unwrap());
		return new HttpResponseAdapter(resp);
	}

	public void mockConnection(String responseBody) throws Exception {
		HTTPResponse response = mock(HTTPResponse.class);
		this.urlFetchServiceMock = mock(URLFetchService.class);

		when(response.getResponseCode()).thenReturn(200);
		when(response.getContent()).thenReturn(responseBody.getBytes());
		when(urlFetchServiceMock.fetch(Mockito.any(HTTPRequest.class)))
				.thenReturn(response);
	}
}
