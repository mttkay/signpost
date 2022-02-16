package oauth.signpost.commonshttp5;

import oauth.signpost.basic.HttpRequestAdapterTestBase;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnit44Runner;

@RunWith(MockitoJUnit44Runner.class)
public class HttpRequestAdapterTest extends HttpRequestAdapterTestBase {

    @Override
    public void prepareRequest() throws Exception {
        HttpPost r = new HttpPost(URL);
        r.setHeader(HEADER_NAME, HEADER_VALUE);
        StringEntity body = new StringEntity(PAYLOAD, ContentType.create(CONTENT_TYPE));
        r.setEntity(body);
        request = new HttpRequestAdapter(r);
    }
}
