package oauth.signpost.signature;

import junit.framework.TestCase;

import java.nio.charset.StandardCharsets;

public class Base64Test extends TestCase {

    public void testEncode() {
        assertEquals("c2lnbnBvc3QtY29yZQ==", Base64.encode("signpost-core".getBytes(StandardCharsets.UTF_8)));
    }
}
