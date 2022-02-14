package oauth.signpost.commonshttp5;

import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.HttpResponse;

import java.io.IOException;
import java.io.InputStream;


public class HttpResponseAdapter implements oauth.signpost.http.HttpResponse {

    private ClassicHttpResponse response;

    public HttpResponseAdapter(ClassicHttpResponse response) {
        this.response = response;
    }

    public InputStream getContent() throws IOException {
        return response.getEntity().getContent();
    }

    public int getStatusCode() throws IOException {
        return response.getCode();
    }

    public String getReasonPhrase() throws Exception {
        return response.getReasonPhrase();
    }

    public Object unwrap() {
        return response;
    }
}
