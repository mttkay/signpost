package oauth.signpost.mocks;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;

import oauth.signpost.basic.DefaultOAuthProvider;
import oauth.signpost.basic.HttpURLConnectionRequestAdapter;
import oauth.signpost.http.HttpRequest;

@SuppressWarnings("serial")
public class DefaultOAuthProviderMock extends DefaultOAuthProvider implements OAuthProviderMock {

    private HttpURLConnection connectionMock;

    public DefaultOAuthProviderMock(String requestTokenUrl, String accessTokenUrl, String websiteUrl) {
        super(requestTokenUrl, accessTokenUrl, websiteUrl);
    }

    public void mockConnection(String responseBody) throws Exception {
        this.connectionMock = mock(HttpURLConnection.class);
        InputStream is = new ByteArrayInputStream(responseBody.getBytes());
        when(connectionMock.getResponseCode()).thenReturn(200);
        when(connectionMock.getInputStream()).thenReturn(is);
    }

    @Override
    protected HttpRequest createRequest(String endpointUrl) throws MalformedURLException,
            IOException {
        return new HttpURLConnectionRequestAdapter(connectionMock);
    }
}
