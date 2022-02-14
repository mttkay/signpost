package oauth.signpost.commonshttp5;

import oauth.signpost.http.HttpRequest;
import oauth.signpost.mocks.OAuthProviderMock;
import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.classic.methods.HttpUriRequest;
import org.apache.hc.core5.http.ClassicHttpRequest;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.ProtocolVersion;
import org.apache.hc.core5.http.io.entity.InputStreamEntity;
import org.apache.hc.core5.http.message.StatusLine;
import org.mockito.Mockito;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SuppressWarnings("serial")
public class CommonHttpOAuthProviderMock extends CommonsHttpOAuthProvider implements
        OAuthProviderMock {

    private HttpClient httpClientMock;

    public CommonHttpOAuthProviderMock(String requestTokenUrl, String accessTokenUrl, String websiteUrl) {
        super(requestTokenUrl, accessTokenUrl, websiteUrl);
    }

    @Override
    protected oauth.signpost.http.HttpResponse sendRequest(HttpRequest request) throws Exception {
        ClassicHttpResponse resp = (ClassicHttpResponse) httpClientMock.execute((ClassicHttpRequest) request.unwrap());
        return new HttpResponseAdapter(resp);
    }

    public void mockConnection(String responseBody) throws Exception {
        ClassicHttpResponse response = mock(ClassicHttpResponse.class);
        this.httpClientMock = mock(HttpClient.class);
        InputStream is = new ByteArrayInputStream(responseBody.getBytes());
        InputStreamEntity entity = new InputStreamEntity(is, responseBody.length(), ContentType.APPLICATION_JSON);
        StatusLine statusLine = new StatusLine(new ProtocolVersion("HTTP", 1, 1), 200, "OK");

//        when(response.getStatusLine()).thenReturn(statusLine);
        when(response.getEntity()).thenReturn(entity);
        when(httpClientMock.execute(Mockito.any(HttpUriRequest.class))).thenReturn(response);
    }
}
