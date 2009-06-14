package oauth.signpost.jetty;

import oauth.signpost.basic.HttpRequestAdapterTestBase;

import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnit44Runner;
import org.mortbay.io.ByteArrayBuffer;
import org.mortbay.jetty.client.HttpExchange;

@RunWith(MockitoJUnit44Runner.class)
public class HttpRequestAdapterTest extends HttpRequestAdapterTestBase {

    @Override
    public void prepareRequest() throws Exception {
        HttpExchange r = new HttpExchange();
        r.setMethod(HTTP_POST_METHOD);
        r.setURL(URL);
        r.addRequestHeader(HEADER_NAME, HEADER_VALUE);
        r.addRequestHeader("Content-Type", CONTENT_TYPE);
        r.setRequestContent(new ByteArrayBuffer(PAYLOAD.getBytes()));
        request = new HttpRequestAdapter(r);
    }
}
