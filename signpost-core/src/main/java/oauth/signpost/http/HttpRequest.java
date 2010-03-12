package oauth.signpost.http;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import oauth.signpost.OAuthConsumer;
import oauth.signpost.basic.HttpURLConnectionRequestAdapter;

/**
 * A concise description of an HTTP request. Contains methods to access all
 * those parts of an HTTP request which Signpost needs to sign a message. If you
 * want to extend Signpost to sign a different kind of HTTP request than those
 * currently supported, you'll have to write an adapter which implements this
 * interface and a custom {@link OAuthConsumer} which performs the wrapping.
 * 
 * @see HttpURLConnectionRequestAdapter
 * @author Matthias Kaeppler
 */
public interface HttpRequest {

    String getMethod();

    String getRequestUrl();

    void setRequestUrl(String url);

    void setHeader(String name, String value);

    String getHeader(String name);

    Map<String, String> getAllHeaders();

    InputStream getMessagePayload() throws IOException;

    String getContentType();

    /**
     * Returns the wrapped request object, in case you must work directly on it.
     * 
     * @return the wrapped request object
     */
    Object unwrap();
}
