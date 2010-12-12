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
        return this.connection.getInputStream();
    }
    
    public InputStream getErrorContent() throws IOException {
		return this.connection.getErrorStream();
	}

    public int getStatusCode() throws IOException {
        return this.connection.getResponseCode();
    }

    public String getReasonPhrase() throws Exception {
        return this.connection.getResponseMessage();
    }

    public Object unwrap() {
        return this.connection;
    }
}
