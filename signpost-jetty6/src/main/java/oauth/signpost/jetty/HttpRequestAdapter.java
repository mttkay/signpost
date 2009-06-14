package oauth.signpost.jetty;

import java.io.IOException;
import java.io.InputStream;

import oauth.signpost.http.HttpRequest;

import org.mortbay.jetty.HttpFields;
import org.mortbay.jetty.client.HttpExchange;

public class HttpRequestAdapter implements HttpRequest {

    private HttpExchange request;

    public HttpRequestAdapter(HttpExchange request) {
        this.request = request;
    }

    public String getContentType() {
        HttpFields fields = request.getRequestFields();
        return fields.getStringField("Content-Type");
    }

    public String getHeader(String name) {
        HttpFields fields = request.getRequestFields();
        return fields.getStringField(name);
    }

    public InputStream getMessagePayload() throws IOException {
        return request.getRequestContentSource();
    }

    public String getMethod() {
        return request.getMethod();
    }

    public String getRequestUrl() {
        return request.getScheme() + "://"
                + request.getAddress().toString().replaceAll(":\\d+", "");
    }

    public boolean hasPayload() {
        return request.getRequestContentSource() != null;
    }

    public void setHeader(String name, String value) {
        request.setRequestHeader(name, value);
    }

}
