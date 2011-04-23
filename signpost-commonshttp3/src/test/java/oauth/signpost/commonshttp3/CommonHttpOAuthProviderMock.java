package oauth.signpost.commonshttp3;

import oauth.signpost.commonshttp3.Http3ResponseAdapter;
import oauth.signpost.commonshttp3.CommonsHttp3OAuthProvider;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import oauth.signpost.http.HttpRequest;
import oauth.signpost.mocks.OAuthProviderMock;

import org.mockito.Mockito;

@SuppressWarnings("serial")
public class CommonHttpOAuthProviderMock extends CommonsHttp3OAuthProvider implements
        OAuthProviderMock {

   // private HttpClient httpClientMock;

    public CommonHttpOAuthProviderMock(String requestTokenUrl, String accessTokenUrl,
            String websiteUrl) {
        super(requestTokenUrl, accessTokenUrl, websiteUrl);
    }

    @Override
    protected oauth.signpost.http.HttpResponse sendRequest(HttpRequest request) throws Exception {
		/*
        HttpResponse resp = httpClientMock.execute((HttpUriRequest) request.unwrap());
        return new Http3ResponseAdapter(resp);
		 * 
		 * 
		 */
		return null;
    }

    public void mockConnection(String responseBody) throws Exception {
		/*
        HttpResponse response = mock(HttpResponse.class);
        this.httpClientMock = mock(HttpClient.class);
        InputStream is = new ByteArrayInputStream(responseBody.getBytes());
        InputStreamEntity entity = new InputStreamEntity(is, responseBody.length());
        StatusLine statusLine = new BasicStatusLine(new ProtocolVersion("HTTP", 1, 1), 200, "OK");

        when(response.getStatusLine()).thenReturn(statusLine);
        when(response.getEntity()).thenReturn(entity);
        when(httpClientMock.execute(Mockito.any(HttpUriRequest.class))).thenReturn(response);
		 * 
		 */
    }
}
