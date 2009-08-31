package oauth.signpost.basic;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;

import oauth.signpost.http.HttpRequest;

public class HttpRequestAdapter implements HttpRequest {

    protected HttpURLConnection connection;

    public HttpRequestAdapter(HttpURLConnection connection) {
        this.connection = connection;
    }

    public String getMethod() {
        return connection.getRequestMethod();
    }

    public String getRequestUrl() {
        return connection.getURL().toExternalForm();
    }

    public void setHeader(String name, String value) {
        connection.setRequestProperty(name, value);
    }

    public String getHeader(String name) {
        return connection.getRequestProperty(name);
    }

    public InputStream getMessagePayload() throws IOException {
        return null;
    }

    public String getContentType() {
        return connection.getRequestProperty("Content-Type");
    }
}
