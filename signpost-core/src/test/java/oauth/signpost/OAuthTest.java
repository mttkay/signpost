package oauth.signpost;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import oauth.signpost.http.HttpParameters;

import org.junit.Test;

public class OAuthTest {

    private String rfc3986ReservedCharacters = ":/?#[]@!$&'()*+,;=";

    private String rfc3986UnreservedCharacters = "-._~"; // alpha-numeric chars ignored

    private String reservedCharactersEncoded = "%3A%2F%3F%23%5B%5D%40%21%24%26%27%28%29%2A%2B%2C%3B%3D";

    /**
     * OAuth percent encoding demands characters from the URI reserved set to be
     * percent encoded, and characters from the unreserved to NOT be percent
     * encoded. All characters must be UTF-8 encoded first.
     */
    @Test
    public void shouldHonorOAuthPercentEncodingRules() {

        // ALWAYS percent encode all characters from the reserved set as per RFC3986
        assertEquals(reservedCharactersEncoded,
                OAuth.percentEncode(rfc3986ReservedCharacters));
        // NEVER percent encode any characters from the unreserved set as per RFC3986
        assertEquals(rfc3986UnreservedCharacters,
                OAuth.percentEncode(rfc3986UnreservedCharacters));
        // percent encode spaces, do not use +
        assertEquals("%20", OAuth.percentEncode(" "));
        // percent encode %
        assertEquals("%25", OAuth.percentEncode("%"));
    }

    @Test
    public void shouldCorrectlyPercentDecodeReservedCharacters() {
        assertEquals(rfc3986ReservedCharacters,
                OAuth.percentDecode(reservedCharactersEncoded));
        assertEquals("%", OAuth.percentDecode("%25"));
        assertEquals(" ", OAuth.percentDecode("%20"));
    }

    @Test
    public void shouldCorrectlyFormEncodeParameters() throws Exception {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("one", rfc3986ReservedCharacters);
        params.put(rfc3986ReservedCharacters, rfc3986UnreservedCharacters);

        // both keys and values must be percent encoded
        assertEquals("one=" + reservedCharactersEncoded + "&" + reservedCharactersEncoded + "="
                + rfc3986UnreservedCharacters, OAuth.formEncode(params.entrySet()));
    }

    @Test
    public void shouldCorrectlyFormDecodeParameters() {
        HttpParameters params = OAuth.decodeForm("one=" + reservedCharactersEncoded
                + "&" + "one=another&"
                + reservedCharactersEncoded + "=" + rfc3986UnreservedCharacters);

        assertTrue(params.size() == 3);

        Iterator<String> iter1 = params.get("one").iterator();
        assertEquals(rfc3986ReservedCharacters, iter1.next());
        assertEquals("another", iter1.next());

        Iterator<String> iter2 = params.get(rfc3986ReservedCharacters).iterator();
        assertEquals(rfc3986UnreservedCharacters, iter2.next());
    }

    @Test
    public void shouldCorrectlyAppendQueryParameters() {
        String url1 = "http://www.example.com";
        assertEquals("http://www.example.com?a=1&b=2",
                OAuth.addQueryParameters(url1, "a", "1", "b", "2"));

        String url2 = "http://www.example.com?x=1";
        assertEquals("http://www.example.com?x=1&a=1&b=2",
                OAuth.addQueryParameters(url2, "a", "1", "b", "2"));

        Map<String, String> params = new LinkedHashMap<String, String>();
        params.put("a", "1");
        params.put("b", "2");
        assertEquals("http://www.example.com?x=1&a=1&b=2", OAuth.addQueryParameters(url2, params));

    }

    @Test
    public void shouldCorrectlyParseOAuthAuthorizationHeader() {
        String header = "OAuth realm=\"http://xyz.com\", oauth_callback=\"oob\"";
        HttpParameters params = OAuth.oauthHeaderToParamsMap(header);
        assertEquals("http://xyz.com", params.getFirst("realm"));
        assertEquals("oob", params.getFirst("oauth_callback"));
    }

    @Test
    public void shouldCorrectlyPrepareOAuthHeader() {
        assertEquals("OAuth realm=\"http://x.com\"", OAuth.prepareOAuthHeader("realm",
            "http://x.com"));
        assertEquals("OAuth realm=\"http://x.com\", oauth_token=\"x%25y\"", OAuth
            .prepareOAuthHeader("realm", "http://x.com", "oauth_token", "x%y"));
    }
}
