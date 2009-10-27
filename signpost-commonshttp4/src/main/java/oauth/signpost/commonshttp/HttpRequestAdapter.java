package oauth.signpost.commonshttp;

import java.io.IOException;
import java.io.InputStream;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.client.methods.HttpUriRequest;

public class HttpRequestAdapter implements oauth.signpost.http.HttpRequest {

    private HttpUriRequest request;

    private HttpEntity entity;

    public HttpRequestAdapter(HttpUriRequest request) {
        this.request = request;
        if (request instanceof HttpEntityEnclosingRequest) {
            entity = ((HttpEntityEnclosingRequest) request).getEntity();
        }
    }

    public String getHeader(String name) {
        Header header = request.getFirstHeader(name);
        if (header == null) {
            return null;
        }
        return header.getValue();
    }

    public String getMethod() {
        return request.getRequestLine().getMethod();
    }

    public String getRequestUrl() {
        return request.getURI().toString();
    }

    public void setHeader(String name, String value) {
        request.setHeader(name, value);
    }

    public String getContentType() {
        if (entity == null) {
            return null;
        }
        Header header = entity.getContentType();
        if (header == null) {
            return null;
        }
        return header.getValue();
    }

    public InputStream getMessagePayload() throws IOException {
        if (entity == null) {
            return null;
        }
        return entity.getContent();
    }
}
