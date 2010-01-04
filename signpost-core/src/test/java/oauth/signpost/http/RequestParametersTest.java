package oauth.signpost.http;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

import java.util.HashMap;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnit44Runner;

@RunWith(MockitoJUnit44Runner.class)
public class RequestParametersTest {

    @Test
    public void testBasicBehavior() {
        RequestParameters params = new RequestParameters();
        assertTrue(params.isEmpty());

        params.putNull("a", "5");
        assertFalse(params.isEmpty());
        assertEquals("5", params.get("a").first());

        params.putNull("a", "1");
        assertEquals("a=1&a=5", params.getFormEncoded("a"));

        params.putNull("b", "drei");
        params.putNull("b", "vier");
        HashMap<String, String> other = new HashMap<String, String>();
        other.put("b", "eins");
        params.putMap(other);

        assertEquals(2, params.keySet().size());
        assertEquals(5, params.size());
        assertEquals("b=drei&b=eins&b=vier", params.getFormEncoded("b"));

        params.putNull("a b", "c d");
        assertEquals("a%20b=c%20d", params.getFormEncoded("a b"));

        assertEquals("x=", params.getFormEncoded("x"));
    }
}
