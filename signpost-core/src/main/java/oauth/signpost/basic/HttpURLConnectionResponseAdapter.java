package oauth.signpost.basic;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;

import oauth.signpost.http.HttpResponse;

public class HttpURLConnectionResponseAdapter implements HttpResponse {

    private HttpURLConnection connection;

    public HttpURLConnectionResponseAdapter(HttpURLConnection connection) {
        this.connection = connection;
    }

    public InputStream getContent() throws IOException {
        return connection.getInputStream();
    }

    public int getStatusCode() throws IOException {
        return connection.getResponseCode();
    }

    public Object unwrap() {
        return connection;
    }
}
