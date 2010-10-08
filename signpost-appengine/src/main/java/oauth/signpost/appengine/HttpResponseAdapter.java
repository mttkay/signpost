package oauth.signpost.appengine;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import oauth.signpost.http.HttpResponse;

import com.google.appengine.api.urlfetch.HTTPResponse;

public class HttpResponseAdapter implements HttpResponse {
	private HTTPResponse response;

	public HttpResponseAdapter(HTTPResponse response) {
		this.response = response;
	}

	public int getStatusCode() throws IOException {
		return response.getResponseCode();
	}

	public String getReasonPhrase() throws Exception {
		return null;
	}

	public InputStream getContent() throws IOException {
		return new ByteArrayInputStream(response.getContent());
	}

	public Object unwrap() {
		return response;
	}

}
