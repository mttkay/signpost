package oauth.signpost.commonshttp5.async;

import oauth.signpost.basic.HttpRequestAdapterTestBase;
import org.apache.hc.client5.http.async.methods.SimpleHttpRequest;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.Method;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class HttpAsyncRequestAdapterTest extends HttpRequestAdapterTestBase {

    @Override
    public void prepareRequest() throws Exception {
        SimpleHttpRequest request = SimpleHttpRequest.create(Method.POST.toString(), URL);
        request.addHeader(HEADER_NAME, HEADER_VALUE);
        request.setBody(PAYLOAD, ContentType.create(CONTENT_TYPE));
        this.request = new HttpAsyncRequestAdapter(request);
    }
}
