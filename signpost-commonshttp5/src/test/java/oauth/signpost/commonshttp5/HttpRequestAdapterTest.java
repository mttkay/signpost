package oauth.signpost.commonshttp5;

import oauth.signpost.basic.HttpRequestAdapterTestBase;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnit44Runner;

import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnit44Runner.class)
public class HttpRequestAdapterTest extends HttpRequestAdapterTestBase {

    @Override
    public void prepareRequest() throws Exception {
        HttpPost r = new HttpPost(URL);
        r.setHeader(HEADER_NAME, HEADER_VALUE);
        StringEntity body = new StringEntity(PAYLOAD, ContentType.TEXT_PLAIN);
        r.setEntity(body);
        request = new HttpRequestAdapter(r);
    }

    @Test
    @Override
    public void shouldReturnCorrectContentType() {
        assertEquals("text/plain; charset=ISO-8859-1", request.getContentType());
    }
}
