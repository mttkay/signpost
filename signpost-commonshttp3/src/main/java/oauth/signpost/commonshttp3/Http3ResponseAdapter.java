package oauth.signpost.commonshttp3;

import java.io.IOException;
import java.io.InputStream;

import oauth.signpost.http.HttpResponse;
import org.apache.commons.httpclient.HttpMethod;

public class Http3ResponseAdapter implements HttpResponse {

	private HttpMethod httpMethod;


    public Http3ResponseAdapter(HttpMethod method) {
		this.httpMethod = method;
    }

    public InputStream getContent() throws IOException {
		return httpMethod.getResponseBodyAsStream();
    }

    public int getStatusCode() throws IOException {
        return httpMethod.getStatusCode();
    }

    public String getReasonPhrase() throws Exception {
        return httpMethod.getStatusLine().getReasonPhrase();
    }

    public Object unwrap() {
        return httpMethod;
    }
}
