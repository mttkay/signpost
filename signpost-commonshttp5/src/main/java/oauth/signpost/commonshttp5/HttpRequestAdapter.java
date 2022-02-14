package oauth.signpost.commonshttp5;

import org.apache.hc.core5.http.ClassicHttpRequest;
import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.HttpEntity;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class HttpRequestAdapter implements oauth.signpost.http.HttpRequest {

    private ClassicHttpRequest request;

    private HttpEntity entity;

    public HttpRequestAdapter(ClassicHttpRequest request) {
        this.request = request;
        this.entity = request.getEntity();
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
        if (entity == null) {
            return null;
        }
        return entity.getContentType();
    }

    public InputStream getMessagePayload() throws IOException {
        if (entity == null) {
            return null;
        }
        return entity.getContent();
    }

    public Object unwrap() {
        return request;
    }
}
