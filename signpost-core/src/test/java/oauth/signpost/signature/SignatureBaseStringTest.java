package oauth.signpost.signature;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.HashMap;

import oauth.signpost.OAuth;
import oauth.signpost.SignpostTestBase;
import oauth.signpost.http.HttpRequest;

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

        // example from OAuth spec
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("a", "1");
        params.put("c", "hi there");
        params.put("f", "25");
        params.put("f", "50");
        params.put("f", "a");
        params.put("z", "p");
        params.put("z", "t");
        String expected = "a=1&c=hi%20there&f=25&f=50&f=a&z=p&z=t";
        String result = new SignatureBaseString(httpGetMock, params).normalizeRequestParameters();
        assertEquals(expected, result);

        // examples from the official test cases on
        // http://oauth.pbwiki.com/TestCases
        params = new HashMap<String, String>();
        params.put("a", "x!y");
        params.put("a", "x y");
        expected = "a=x%20y&a=x%21y";
        result = new SignatureBaseString(httpGetMock, params).normalizeRequestParameters();
        assertEquals(expected, result);

        params = new HashMap<String, String>();
        params.put("name", "");
        expected = "name=";
        result = new SignatureBaseString(httpGetMock, params).normalizeRequestParameters();
        assertEquals(expected, result);
    }

    // @Test
    // public void shouldIncludeOAuthAndQueryAndBodyParams() throws Exception {
    //
    // HashMap<String, String> params = new HashMap<String, String>();
    //
    // HttpRequest request = mock(HttpRequest.class);
    // when(request.getRequestUrl()).thenReturn("http://example.com?a=1");
    // ByteArrayInputStream body = new ByteArrayInputStream("b=2".getBytes());
    // when(request.getMessagePayload()).thenReturn(body);
    // when(request.getContentType()).thenReturn(
    // "application/x-www-form-urlencoded");
    // when(request.getHeader("Authorization")).thenReturn(
    // "OAuth realm=www.example.com, oauth_signature=12345");
    //
    // SignatureBaseString sbs = new SignatureBaseString(request);
    // String result = sbs.generate();
    //
    // assertTrue(result.contains("a%3D1"));
    // assertTrue(result.contains("b%3D2"));
    // assertTrue(result.contains("oauth_consumer_key%3D" + CONSUMER_KEY));
    // assertTrue(result.contains("oauth_signature_method%3D"
    // + SIGNATURE_METHOD));
    // assertTrue(result.contains("oauth_timestamp%3D" + TIMESTAMP));
    // assertTrue(result.contains("oauth_nonce%3D" + NONCE));
    // assertTrue(result.contains("oauth_version%3D" + OAUTH_VERSION));
    // assertTrue(result.contains("oauth_token%3D" + TOKEN));
    //
    // // should ignore signature and realm params
    // assertFalse(result.contains("oauth_signature%3D12345"));
    // assertFalse(result.contains("realm%3Dwww.example.com"));
    //
    // // should not include the body param if not x-www-form-urlencoded
    // when(request.getContentType()).thenReturn(null);
    // sbs = new SignatureBaseString(request);
    // assertFalse(sbs.generate().contains("b%3D2"));
    // }

    @Test
    public void shouldAlwaysIncludeTokenParamEvenWhenEmpty() throws Exception {

        SignatureBaseString sbs = new SignatureBaseString(httpGetMock, EMPTY_PARAMS);
        String result = sbs.generate();

        assertTrue(result.contains(OAuth.percentEncode("oauth_token=&")));
    }

    @Test
    public void shouldEncodeAndConcatenateAllSignatureParts() throws Exception {
        HttpRequest request = mock(HttpRequest.class);
        when(request.getMethod()).thenReturn("GET");
        when(request.getRequestUrl()).thenReturn("http://example.com");

        HashMap<String, String> params = new HashMap<String, String>();
        params.put("a", "1");

        SignatureBaseString sbs = new SignatureBaseString(request, params);

        //TODO: Is it correct that a trailing slash is always added to the
        //request URL authority if the path is empty? 
        assertEquals("GET&http%3A%2F%2Fexample.com%2F&a%3D1", sbs.generate());
    }
}
