package oauth.signpost.signature;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import oauth.signpost.SignpostTestBase;
import oauth.signpost.http.HttpRequest;
import oauth.signpost.http.HttpParameters;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnit44Runner;

@RunWith(MockitoJUnit44Runner.class)
public class SignatureBaseStringTest extends SignpostTestBase {

    @Test
    public void shouldConsistOf3NonEmptyPartsConcatenatedWithAmpersand()
            throws Exception {
        SignatureBaseString sbs = new SignatureBaseString(httpPostMock, OAUTH_PARAMS);
        String result = sbs.generate();

        String[] parts = result.split("&");

        assertEquals(3, parts.length);
        assertNotNull(parts[0]);
        assertNotNull(parts[1]);
        assertNotNull(parts[2]);
    }

    @Test
    public void shouldStartWithUppercaseHttpMethod() throws Exception {
        assertTrue(new SignatureBaseString(httpPostMock, EMPTY_PARAMS).generate().split("&")[0]
            .equals("POST"));

        assertTrue(new SignatureBaseString(httpGetMock, EMPTY_PARAMS).generate().split("&")[0]
            .equals("GET"));
    }

    @Test
    public void shouldNormalizeRequestUrl() throws Exception {
        // must include scheme and authority in lowercase letters,
        // plus non HTTP(S) port, plus path,
        // but must ignore query params and fragment
        when(httpGetMock.getRequestUrl())
            .thenReturn("HTTP://www.Example.Com:123/test?q=1#fragment");
        assertEquals("http://www.example.com:123/test", new SignatureBaseString(httpGetMock,
            OAUTH_PARAMS).normalizeRequestUrl());

        // must exclude HTTP(S) default ports
        when(httpGetMock.getRequestUrl()).thenReturn("http://example.com:80");
        assertEquals("http://example.com/", new SignatureBaseString(httpGetMock, EMPTY_PARAMS)
            .normalizeRequestUrl());
        when(httpGetMock.getRequestUrl()).thenReturn("https://example.com:443");
        assertEquals("https://example.com/", new SignatureBaseString(httpGetMock, EMPTY_PARAMS)
            .normalizeRequestUrl());
    }

    @Test
    public void shouldNormalizeParameters() throws Exception {

        // should ignore signature, callback, and realm params
        HttpParameters params = new HttpParameters();
        params.put("a", "1", true);
        params.put("realm", "www.example.com", true);
        params.put("oauth_signature", "12345", true);
        String result = new SignatureBaseString(httpGetMock, params).normalizeRequestParameters();
        assertEquals("a=1", result);

        // example from OAuth spec
        params = new HttpParameters();
        params.put("a", "1", true);
        params.put("c", "hi there", true);
        params.put("f", "25", true);
        params.put("f", "50", true);
        params.put("f", "a", true);
        params.put("z", "p", true);
        params.put("z", "t", true);
        String expected = "a=1&c=hi%20there&f=25&f=50&f=a&z=p&z=t";
        result = new SignatureBaseString(httpGetMock, params).normalizeRequestParameters();
        assertEquals(expected, result);

        // examples from the official test cases on
        // http://oauth.pbwiki.com/TestCases
        params = new HttpParameters();
        params.put("a", "x!y", true);
        params.put("a", "x y", true);
        expected = "a=x%20y&a=x%21y";
        result = new SignatureBaseString(httpGetMock, params).normalizeRequestParameters();
        assertEquals(expected, result);

        params = new HttpParameters();
        params.put("name", "", true);
        assertEquals("name=", new SignatureBaseString(httpGetMock, params)
            .normalizeRequestParameters());
        params.putNull("name", null);
        assertEquals("name=", new SignatureBaseString(httpGetMock, params)
            .normalizeRequestParameters());
    }

    @Test
    public void shouldEncodeAndConcatenateAllSignatureParts() throws Exception {
        HttpRequest request = mock(HttpRequest.class);
        when(request.getMethod()).thenReturn("GET");
        when(request.getRequestUrl()).thenReturn("http://example.com");

        HttpParameters params = new HttpParameters();
        params.put("a", "1");

        SignatureBaseString sbs = new SignatureBaseString(request, params);

        //TODO: Is it correct that a trailing slash is always added to the
        //request URL authority if the path is empty? 
        assertEquals("GET&http%3A%2F%2Fexample.com%2F&a%3D1", sbs.generate());
    }
}
