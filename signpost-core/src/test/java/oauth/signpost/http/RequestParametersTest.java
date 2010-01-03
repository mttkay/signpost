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

        params.put("a", "5");
        assertFalse(params.isEmpty());
        assertEquals("a=5", params.get("a"));

        params.put("a", "1");
        assertEquals("a=1&a=5", params.get("a"));

        params.put("b", "drei");
        params.put("b", "vier");
        HashMap<String, String> other = new HashMap<String, String>();
        other.put("b", "eins");
        params.putAll(other);

        assertEquals(2, params.keySet().size());
        assertEquals(5, params.values().size());
        assertEquals("b=drei&b=eins&b=vier", params.get("b"));

        params.put("a b", "c d");
        assertEquals("a%20b=c%20d", params.get("a b"));

        assertEquals("x=", params.get("x"));
    }
}
