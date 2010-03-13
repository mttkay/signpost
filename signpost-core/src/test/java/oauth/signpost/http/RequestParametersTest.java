package oauth.signpost.http;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnit44Runner;

@RunWith(MockitoJUnit44Runner.class)
public class RequestParametersTest {

    @Test
    public void testBasicBehavior() {
        HttpParameters params = new HttpParameters();
        assertTrue(params.isEmpty());

        params.put("a", "5");
        assertFalse(params.isEmpty());
        assertEquals("5", params.get("a").first());

        params.put("a", "1");
        assertEquals("a=1&a=5", params.getAsQueryString("a"));

        params.put("b", "drei");
        params.put("b", "vier");
        HashMap<String, List<String>> other = new HashMap<String, List<String>>();
        LinkedList<String> values = new LinkedList<String>();
        values.add("eins");
        other.put("b", values);
        params.putMap(other);

        assertEquals(2, params.keySet().size());
        assertEquals(5, params.size());
        assertEquals("b=drei&b=eins&b=vier", params.getAsQueryString("b"));

        params.put("a b", "c d", true);
        assertEquals("a%20b=c%20d", params.getAsQueryString("a b"));
        assertEquals("c%20d", params.getFirst("a%20b"));
        assertEquals("c d", params.getFirst("a%20b", true));

        assertEquals("x=", params.getAsQueryString("x"));

        params.clear();
        assertTrue(params.isEmpty());
        assertEquals(0, params.size());
        assertEquals(null, params.get("a"));

        String[] kvPairs = new String[] { "a", "1", "b", "2" };
        params.putAll(kvPairs, false);
        assertEquals("1", params.getFirst("a"));
        assertEquals("2", params.getFirst("b"));
    }
}
