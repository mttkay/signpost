package oauth.signpost.http;

import java.io.IOException;
import java.io.InputStream;

public interface HttpRequest {

    String getMethod();

    String getRequestUrl();

    void setHeader(String name, String value);

    String getHeader(String name);

    InputStream getMessagePayload() throws IOException;

    String getContentType();
}
