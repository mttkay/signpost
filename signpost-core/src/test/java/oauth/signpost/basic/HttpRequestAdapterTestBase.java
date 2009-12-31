package oauth.signpost.basic;

import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import oauth.signpost.http.HttpRequest;

import org.junit.Before;
import org.junit.Test;

public abstract class HttpRequestAdapterTestBase {

    protected static final String URL = "http://www.example.com/protected";

    protected static final String HTTP_POST_METHOD = "POST";

    protected static final String CONTENT_TYPE = "text/plain";

    protected static final String HEADER_NAME = "test-header";

    protected static final String HEADER_VALUE = "test-header-value";

    protected static final String PAYLOAD = "message-body";

    protected HttpRequest request;

    @Before
    public abstract void prepareRequest() throws Exception;

    @Test
    public void shouldReturnCorrectRequestUrl() {
        assertEquals(URL, request.getRequestUrl());
    }

    @Test
    public void shouldReturnCorrectRequestMethod() {
        assertEquals(HTTP_POST_METHOD, request.getMethod());
    }

    @Test
    public void shouldGetAndSetRequestHeaders() {
        assertEquals(HEADER_VALUE, request.getHeader(HEADER_NAME));

        request.setHeader("a", "b");
        assertEquals("b", request.getHeader("a"));

        assertTrue(request.getAllHeaders().containsKey(HEADER_NAME));
        assertTrue(request.getAllHeaders().containsKey("a"));
    }

    @Test
    public void shouldReturnCorrectContentType() {
        assertEquals(CONTENT_TYPE, request.getContentType());
    }

    @Test
    public void shouldReturnCorrectMessagePayload() throws Exception {
        String actual = new BufferedReader(new InputStreamReader(
                request.getMessagePayload())).readLine();
        assertEquals(PAYLOAD, actual);
    }
}
