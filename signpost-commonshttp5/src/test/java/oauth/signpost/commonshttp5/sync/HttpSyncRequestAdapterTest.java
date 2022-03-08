package oauth.signpost.commonshttp5.sync;

import oauth.signpost.basic.HttpRequestAdapterTestBase;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class HttpSyncRequestAdapterTest extends HttpRequestAdapterTestBase {

    @Override
    public void prepareRequest() throws Exception {
        HttpPost r = new HttpPost(URL);
        r.setHeader(HEADER_NAME, HEADER_VALUE);
        StringEntity body = new StringEntity(PAYLOAD, ContentType.create(CONTENT_TYPE));
        r.setEntity(body);
        request = new HttpSyncRequestAdapter(r);
    }
}
