package oauth.signpost;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

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
        ArrayList<Parameter> params = new ArrayList<Parameter>(2);
        params.add(new Parameter("one", rfc3986ReservedCharacters));
        params.add(new Parameter(rfc3986ReservedCharacters,
                rfc3986UnreservedCharacters));

        // both keys and values must be percent encoded
        assertEquals(
                "one=" + reservedCharactersEncoded + "&"
                        + reservedCharactersEncoded + "="
                        + rfc3986UnreservedCharacters, OAuth.formEncode(params));
    }

    @Test
    public void shouldCorrectlyFormDecodeParameters() {
        Collection<Parameter> params = null;
        params = OAuth.decodeForm("one=" + reservedCharactersEncoded + "&"
                + reservedCharactersEncoded + "=" + rfc3986UnreservedCharacters);

        assertTrue(params.size() == 2);
        Iterator<Parameter> iter = params.iterator();

        Parameter one = iter.next();
        assertEquals("one", one.getKey());
        assertEquals(rfc3986ReservedCharacters, one.getValue());

        Parameter two = iter.next();
        assertEquals(rfc3986ReservedCharacters, two.getKey());
        assertEquals(rfc3986UnreservedCharacters, two.getValue());
    }

    @Test
    public void shouldCorrectlyAppendQueryParameters() {
        String url1 = "http://www.example.com";
        assertEquals("http://www.example.com?a=1&b=2",
                OAuth.addQueryParameters(url1, "a", "1", "b", "2"));

        String url2 = "http://www.example.com?x=1";
        assertEquals("http://www.example.com?x=1&a=1&b=2",
                OAuth.addQueryParameters(url2, "a", "1", "b", "2"));
    }
}
