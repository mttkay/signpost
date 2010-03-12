package oauth.signpost.basic;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import oauth.signpost.http.HttpRequest;

public class HttpURLConnectionRequestAdapter implements HttpRequest {

    protected HttpURLConnection connection;

    public HttpURLConnectionRequestAdapter(HttpURLConnection connection) {
        this.connection = connection;
    }

    public String getMethod() {
        return connection.getRequestMethod();
    }

    public String getRequestUrl() {
        return connection.getURL().toExternalForm();
    }

    public void setRequestUrl(String url) {
        // can't do
    }

    public void setHeader(String name, String value) {
        connection.setRequestProperty(name, value);
    }

    public String getHeader(String name) {
        return connection.getRequestProperty(name);
    }

    public Map<String, String> getAllHeaders() {
        Map<String, List<String>> origHeaders = connection.getRequestProperties();
        Map<String, String> headers = new HashMap<String, String>(origHeaders.size());
        for (String name : origHeaders.keySet()) {
            List<String> values = origHeaders.get(name);
            if (!values.isEmpty()) {
                headers.put(name, values.get(0));
            }
        }
        return headers;
    }

    public InputStream getMessagePayload() throws IOException {
        return null;
    }

    public String getContentType() {
        return connection.getRequestProperty("Content-Type");
    }

    public HttpURLConnection unwrap() {
        return connection;
    }
}
