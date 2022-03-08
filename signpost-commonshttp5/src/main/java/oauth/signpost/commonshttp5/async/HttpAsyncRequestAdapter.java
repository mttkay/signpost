package oauth.signpost.commonshttp5.async;

import org.apache.hc.client5.http.async.methods.SimpleHttpRequest;
import org.apache.hc.core5.http.Header;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class HttpAsyncRequestAdapter implements oauth.signpost.http.HttpRequest {

    private SimpleHttpRequest request;


    public HttpAsyncRequestAdapter(SimpleHttpRequest request) {
        this.request = request;
        this.request.setAbsoluteRequestUri(true);
    }

    public String getMethod() {
        return request.getMethod();
    }

    public String getRequestUrl() {
        return request.getRequestUri();
    }

    public void setRequestUrl(String url) {
        throw new RuntimeException(new UnsupportedOperationException());
    }

    public String getHeader(String name) {
        Header header = request.getFirstHeader(name);
        if (header == null) {
            return null;
        }
        return header.getValue();
    }

    public void setHeader(String name, String value) {
        request.setHeader(name, value);
    }

    public Map<String, String> getAllHeaders() {
        Header[] origHeaders = request.getHeaders();
        HashMap<String, String> headers = new HashMap<String, String>();
        for (Header h : origHeaders) {
            headers.put(h.getName(), h.getValue());
        }
        return headers;
    }

    public String getContentType() {
        return request.getContentType().toString();
    }

    public InputStream getMessagePayload() throws IOException {
        return new ByteArrayInputStream(request.getBody().getBodyBytes());
    }

    public Object unwrap() {
        return request;
    }
}
