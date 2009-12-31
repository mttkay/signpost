package oauth.signpost.http;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

public interface HttpRequest {

    String getMethod();

    String getRequestUrl();

    void setHeader(String name, String value);

    String getHeader(String name);

    Map<String, String> getAllHeaders();

    InputStream getMessagePayload() throws IOException;

    String getContentType();
}
