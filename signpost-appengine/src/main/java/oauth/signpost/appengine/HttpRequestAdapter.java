package oauth.signpost.appengine;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import oauth.signpost.http.HttpRequest;

import com.google.appengine.api.urlfetch.HTTPHeader;
import com.google.appengine.api.urlfetch.HTTPRequest;

public class HttpRequestAdapter implements HttpRequest {

	private HTTPRequest request;

	public HttpRequestAdapter(HTTPRequest request) {
		this.request = request;
	}

	public String getMethod() {
		return request.getMethod().name();
	}

	public String getRequestUrl() {
		return request.getURL().toString();
	}

	public void setRequestUrl(String url) {
		throw new RuntimeException(new UnsupportedOperationException());
	}

	public String getHeader(String name) {
		for (HTTPHeader h : request.getHeaders()) {
			if (h.getName().equals(name))
				return h.getValue();
		}
		return null;
	}

	public void setHeader(String name, String value) {
		request.addHeader(new HTTPHeader(name, value));
	}

	public Map<String, String> getAllHeaders() {
		HashMap<String, String> headers = new HashMap<String, String>();
		for (HTTPHeader h : request.getHeaders()) {
			headers.put(h.getName(), h.getValue());
		}
		return headers;
	}

	public String getContentType() {
		return getHeader("Content-Type");
	}

	public InputStream getMessagePayload() throws IOException {
		if (request.getPayload() == null) {
			return null;
		}
		return new ByteArrayInputStream(request.getPayload());
	}

	public Object unwrap() {
		return request;
	}
}
