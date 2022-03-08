package oauth.signpost.commonshttp5.async;

import org.apache.hc.client5.http.async.methods.SimpleHttpResponse;
import org.apache.hc.core5.http.ClassicHttpResponse;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;


public class HttpAsyncResponseAdapter implements oauth.signpost.http.HttpResponse {

    private SimpleHttpResponse response;

    public HttpAsyncResponseAdapter(SimpleHttpResponse response) {
        this.response = response;
    }

    public InputStream getContent() throws IOException {
        byte[] bytes = response.getBodyBytes();
        return bytes == null ? new ByteArrayInputStream(new byte[0]) : new ByteArrayInputStream(bytes);
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
