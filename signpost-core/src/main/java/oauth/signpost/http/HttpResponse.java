package oauth.signpost.http;

import java.io.IOException;
import java.io.InputStream;

public interface HttpResponse {

    int getStatusCode() throws IOException;

    String getReasonPhrase() throws Exception;

    InputStream getContent() throws IOException;

    /**
     * Returns the underlying response object, in case you need to work on it
     * directly.
     * 
     * @return the wrapped response object
     */
    Object unwrap();
}
