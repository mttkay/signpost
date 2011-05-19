package oauth.signpost.commonshttp3;

import oauth.signpost.commonshttp3.Http3ResponseAdapter;
import oauth.signpost.commonshttp3.CommonsHttp3OAuthProvider;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import oauth.signpost.http.HttpRequest;
import oauth.signpost.mocks.OAuthProviderMock;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.StatusLine;
import org.apache.commons.httpclient.methods.PostMethod;

import org.mockito.Mockito;

@SuppressWarnings("serial")
public class CommonHttpOAuthProviderMock extends CommonsHttp3OAuthProvider implements
        OAuthProviderMock {

	private HttpMethod httpMethodMock;

    public CommonHttpOAuthProviderMock(String requestTokenUrl, String accessTokenUrl,
            String websiteUrl) {
        super(requestTokenUrl, accessTokenUrl, websiteUrl);
    }

    @Override
    protected oauth.signpost.http.HttpResponse sendRequest(HttpRequest request) throws Exception {

        return new Http3ResponseAdapter(this.httpMethodMock);
    }

    public void mockConnection(String responseBody) throws Exception {
		

        InputStream is = new ByteArrayInputStream(responseBody.getBytes());
        StatusLine statusLine = new StatusLine("HTTP/1.1 200 OK");

		this.httpMethodMock = mock(HttpMethod.class);
	   
        when(httpMethodMock.getStatusLine()).thenReturn(statusLine);
        when(httpMethodMock.getStatusCode()).thenReturn(200);
        when(httpMethodMock.getResponseBodyAsStream()).thenReturn(is);
    }
}
