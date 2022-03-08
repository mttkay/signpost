package oauth.signpost.commonshttp5.async;

import oauth.signpost.http.HttpRequest;
import oauth.signpost.mocks.OAuthProviderMock;
import org.apache.hc.client5.http.async.methods.SimpleBody;
import org.apache.hc.client5.http.async.methods.SimpleHttpRequest;
import org.apache.hc.client5.http.async.methods.SimpleHttpResponse;
import org.apache.hc.client5.http.impl.async.CloseableHttpAsyncClient;
import org.apache.hc.core5.concurrent.FutureCallback;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.ProtocolVersion;
import org.apache.hc.core5.http.io.entity.InputStreamEntity;
import org.apache.hc.core5.http.message.StatusLine;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.concurrent.CompletableFuture;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SuppressWarnings("serial")
public class CommonHttpAsyncOAuthProviderMock extends CommonsHttpAsyncOAuthProvider implements OAuthProviderMock {

    private CloseableHttpAsyncClient httpClientMock;

    public CommonHttpAsyncOAuthProviderMock(String requestTokenUrl, String accessTokenUrl, String websiteUrl) {
        super(requestTokenUrl, accessTokenUrl, websiteUrl);
    }

    @Override
    protected oauth.signpost.http.HttpResponse sendRequest(HttpRequest request) throws Exception {
        SimpleHttpResponse response = httpClientMock.execute((SimpleHttpRequest) request.unwrap(), new FutureCallback<SimpleHttpResponse>() {
            @Override
            public void completed(SimpleHttpResponse simpleHttpResponse) {
            }

            @Override
            public void failed(Exception e) {
            }

            @Override
            public void cancelled() {
            }
        }).get();
        return new HttpAsyncResponseAdapter(response);
    }

    public void mockConnection(String responseBody) throws Exception {
        SimpleHttpResponse response = mock(SimpleHttpResponse.class);
        this.httpClientMock = mock(CloseableHttpAsyncClient.class);
        InputStream is = new ByteArrayInputStream(responseBody.getBytes());
        InputStreamEntity entity = new InputStreamEntity(is, responseBody.length(), ContentType.APPLICATION_JSON);
        StatusLine statusLine = new StatusLine(new ProtocolVersion("HTTP", 1, 1), 200, "OK");

        when(response.getBodyBytes()).thenReturn(responseBody.getBytes());
        when(httpClientMock.execute(any(SimpleHttpRequest.class), any())).thenReturn(CompletableFuture.completedFuture(response));
    }
}
