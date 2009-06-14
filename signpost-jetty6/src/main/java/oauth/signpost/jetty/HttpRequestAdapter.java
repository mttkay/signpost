package oauth.signpost.jetty;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import oauth.signpost.http.HttpRequest;

import org.mortbay.jetty.HttpFields;
import org.mortbay.jetty.client.HttpExchange;

public class HttpRequestAdapter implements HttpRequest {

    private HttpExchange request;

    private String requestUrl;

    public HttpRequestAdapter(HttpExchange request) {
        this.request = request;
        buildRequestUrl();
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
        return new ByteArrayInputStream(request.getRequestContent().array());
    }

    public String getMethod() {
        return request.getMethod();
    }

    public String getRequestUrl() {
        return requestUrl;
    }

    public boolean hasPayload() {
        return request.getRequestContent() != null;
    }

    public void setHeader(String name, String value) {
        request.setRequestHeader(name, value);
    }

    // Jetty has some very weird mechanism for handling URLs... we have to
    // reconstruct it here.
    private void buildRequestUrl() {
        StringBuilder sb = new StringBuilder();
        sb.append(request.getScheme() + "://");
        sb.append(request.getAddress().toString().replaceAll(":\\d+", ""));
        if (request.getURI() != null) {
            // the "URI" in Jetty is actually the path... WTF?!
            sb.append(request.getURI());
        }
        this.requestUrl = sb.toString();
    }
}
