package oauth.signpost.commonshttp;

import java.io.IOException;
import java.io.InputStream;


public class HttpResponseAdapter implements oauth.signpost.http.HttpResponse {

    private org.apache.http.HttpResponse response;

    public HttpResponseAdapter(org.apache.http.HttpResponse response) {
        this.response = response;
    }

    public InputStream getContent() throws IOException {
        return response.getEntity().getContent();
    }

    public int getStatusCode() throws IOException {
        return response.getStatusLine().getStatusCode();
    }

    public String getReasonPhrase() throws Exception {
        return response.getStatusLine().getReasonPhrase();
    }

    public Object unwrap() {
        return response;
    }
}
