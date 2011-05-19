package oauth.signpost.commonshttp3;

import oauth.signpost.commonshttp3.Http3RequestAdapter;
import oauth.signpost.basic.HttpRequestAdapterTestBase;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.methods.StringRequestEntity;

import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnit44Runner;

@RunWith(MockitoJUnit44Runner.class)
public class HttpRequestAdapterTest extends HttpRequestAdapterTestBase {

    @Override
    public void prepareRequest() throws Exception {
        PostMethod method = new PostMethod(URL);
        method.setRequestHeader(HEADER_NAME, HEADER_VALUE);
        RequestEntity body = new StringRequestEntity(PAYLOAD,CONTENT_TYPE,null);
        method.setRequestEntity(body);
        request = new Http3RequestAdapter(method);
    }
}
