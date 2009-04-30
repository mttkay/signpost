package oauth.signpost.signature;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.HashSet;

import oauth.signpost.Parameter;
import oauth.signpost.SignpostTestBase;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnit44Runner;

@RunWith(MockitoJUnit44Runner.class)
public class SignatureBaseStringTest extends SignpostTestBase {

    @Test
    public void shouldConsistOf3NonEmptyPartsConcatenatedWithAmpersand()
            throws Exception {
        SignatureBaseString sbs = new SignatureBaseString(new HttpPost(
                "http://example.com"), OAUTH_PARAMS);
        String result = sbs.compute();

        String[] parts = result.split("&");

        assertEquals(3, parts.length);
        assertNotNull(parts[0]);
        assertNotNull(parts[1]);
        assertNotNull(parts[2]);
    }

    @Test
    public void shouldStartWithUppercaseHttpMethod() throws Exception {
        assertTrue(new SignatureBaseString(new HttpPost("http://example.com"),
                OAUTH_PARAMS).compute().split("&")[0].equals("POST"));

        assertTrue(new SignatureBaseString(new HttpGet("http://example.com"),
                OAUTH_PARAMS).compute().split("&")[0].equals("GET"));
    }

    @Test
    public void shouldNormalizeRequestUrl() throws Exception {
        String inputUrl = "HTTP://www.Example.Com:123/test?q=1#fragment";
        String outputUrl = new SignatureBaseString(new HttpGet(), OAUTH_PARAMS).normalizeUrl(inputUrl);

        // must include scheme and authority in lowercase letters,
        // plus non HTTP(S) port, plus path,
        // but must ignore query params and fragment
        assertTrue(outputUrl.equals("http://www.example.com:123/test"));

        // must exclude HTTP(S) default ports
        String expected = "http://example.com";
        assertFalse(new SignatureBaseString(new HttpGet(), OAUTH_PARAMS).normalizeUrl(
                "http://example.com:80").equals(expected));
        assertFalse(new SignatureBaseString(new HttpGet(), OAUTH_PARAMS).normalizeUrl(
                "https://example.com:443").equals(expected));
    }

    @Test
    public void shouldNormalizeParameters() throws Exception {

        HashMap<String, String> oauthParams = new HashMap<String, String>();

        // example from OAuth spec
        HashSet<Parameter> params = new HashSet<Parameter>();
        params.add(new Parameter("a", "1"));
        params.add(new Parameter("c", "hi there"));
        params.add(new Parameter("f", "25"));
        params.add(new Parameter("f", "50"));
        params.add(new Parameter("f", "a"));
        params.add(new Parameter("z", "p"));
        params.add(new Parameter("z", "t"));
        String expected = "a=1&c=hi%20there&f=25&f=50&f=a&z=p&z=t";
        String result = new SignatureBaseString(new HttpGet(), oauthParams).normalizeParameters(params);
        assertEquals(expected, result);

        // examples from the official test cases on http://oauth.pbwiki.com/TestCases
        params = new HashSet<Parameter>();
        params.add(new Parameter("a", "x!y"));
        params.add(new Parameter("a", "x y"));
        expected = "a=x%20y&a=x%21y";
        result = new SignatureBaseString(new HttpGet(), oauthParams).normalizeParameters(params);
        assertEquals(expected, result);

        params = new HashSet<Parameter>();
        params.add(new Parameter("name", ""));
        expected = "name=";
        result = new SignatureBaseString(new HttpGet(), oauthParams).normalizeParameters(params);
        assertEquals(expected, result);
    }

    @Test
    public void shouldIncludeOAuthAndQueryAndBodyParams() throws Exception {

        HttpPost request = new HttpPost("http://example.com?a=1");
        request.addHeader("Authorization", "oauth_signature=1");
        StringEntity body = new StringEntity("b=2");
        body.setContentType("application/x-www-form-urlencoded");
        request.setEntity(body);

        HashMap<String, String> oauthParams = new HashMap<String, String>(
                OAUTH_PARAMS);
        oauthParams.put("oauth_signature", "12345");

        SignatureBaseString sbs = new SignatureBaseString(request, oauthParams);
        String result = sbs.compute();

        assertTrue(result.contains("a%3D1"));
        assertTrue(result.contains("b%3D2"));
        assertTrue(result.contains("oauth_consumer_key%3D" + CONSUMER_KEY));
        assertTrue(result.contains("oauth_signature_method%3D"
                + SIGNATURE_METHOD));
        assertTrue(result.contains("oauth_timestamp%3D" + TIMESTAMP));
        assertTrue(result.contains("oauth_nonce%3D" + NONCE));
        assertTrue(result.contains("oauth_version%3D" + OAUTH_VERSION));
        assertTrue(result.contains("oauth_token%3D" + TOKEN));

        // should ignore signature param
        assertFalse(result.contains("oauth_signature%3D12345"));

        // should not include the body param if not x-www-form-urlencoded
        request.setEntity(new StringEntity("b=2"));
        sbs = new SignatureBaseString(request, oauthParams);
        assertFalse(sbs.compute().contains("b%3D2"));
    }

    @Test
    public void shouldEncodeAndConcatenateAllSignatureParts() throws Exception {
        HttpGet request = new HttpGet("http://example.com?a=1");
        HashMap<String, String> oauthParams = new HashMap<String, String>();
        SignatureBaseString sbs = new SignatureBaseString(request, oauthParams);

        //TODO: Is it correct that a trailing slash is always added to the
        //request URL authority if the path is empty? 
        assertEquals("GET&http%3A%2F%2Fexample.com%2F&a%3D1", sbs.compute());
    }
}
