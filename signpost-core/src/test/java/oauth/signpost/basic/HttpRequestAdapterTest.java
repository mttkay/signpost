package oauth.signpost.basic;

import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.net.HttpURLConnection;
import java.net.URL;

import static org.junit.Assert.assertTrue;

@RunWith(MockitoJUnitRunner.class)
public class HttpRequestAdapterTest extends HttpRequestAdapterTestBase {

    @Override
    public void prepareRequest() throws Exception {
        HttpURLConnection conn = (HttpURLConnection) new URL(URL).openConnection();
        conn.setRequestMethod(HTTP_POST_METHOD);
        conn.setRequestProperty(HEADER_NAME, HEADER_VALUE);
        conn.setRequestProperty("Content-Type", CONTENT_TYPE);
        request = new HttpURLConnectionRequestAdapter(conn);
    }

    @Override
    public void shouldReturnCorrectMessagePayload() throws Exception {
        // can't test this here, because we would have to establish a connection
        assertTrue(true);
    }
}
