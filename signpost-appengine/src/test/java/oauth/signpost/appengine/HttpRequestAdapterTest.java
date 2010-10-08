package oauth.signpost.appengine;

import oauth.signpost.basic.HttpRequestAdapterTestBase;

import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnit44Runner;

import com.google.appengine.api.urlfetch.HTTPHeader;
import com.google.appengine.api.urlfetch.HTTPMethod;
import com.google.appengine.api.urlfetch.HTTPRequest;

@RunWith(MockitoJUnit44Runner.class)
public class HttpRequestAdapterTest extends HttpRequestAdapterTestBase {

	@Override
	public void prepareRequest() throws Exception {
		HTTPRequest r = new HTTPRequest(new java.net.URL(URL), HTTPMethod.POST);
		r.addHeader(new HTTPHeader(HEADER_NAME, HEADER_VALUE));
		r.setPayload(PAYLOAD.getBytes());
		r.addHeader(new HTTPHeader("Content-Type", CONTENT_TYPE));
		request = new HttpRequestAdapter(r);
	}
}
