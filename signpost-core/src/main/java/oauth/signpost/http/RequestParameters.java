package oauth.signpost.http;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import oauth.signpost.OAuth;

public class RequestParameters implements Map<String, SortedSet<String>> {

    private static final long serialVersionUID = -2281503352590395824L;

    private TreeMap<String, SortedSet<String>> wrappedMap = new TreeMap<String, SortedSet<String>>();

    public SortedSet<String> put(String key, SortedSet<String> value) {
        return wrappedMap.put(key, value);
    }

    /**
     * Convenience method to add a single value for the parameter specified by
     * 'key'.
     * 
     * @param key
     *        the parameter name
     * @param value
     *        the parameter value
     * @return the value
     */
    public String put(String key, String value) {
        SortedSet<String> values = wrappedMap.get(key);
        if (values == null) {
            values = new TreeSet<String>();
            wrappedMap.put(OAuth.percentEncode(key), values);
        }
        value = OAuth.percentEncode(value);
        values.add(value);

        return value;
    }

    /**
     * Convenience method to allow for storing null values. {@link #put} doesn't
     * allow null values, because that would be ambiguous.
     * 
     * @param key
     *        the parameter name
     * @param nullString
     *        can be anything, but probably... null?
     * @return null
     */
    public String putNull(String key, String nullString) {
        return put(key, nullString);
    }

    public void putAll(Map<? extends String, ? extends SortedSet<String>> m) {
        wrappedMap.putAll(m);
    }

    /**
     * Convenience method to merge a Map<String, String>.
     * 
     * @param m
     *        the map
     */
    public void putMap(Map<? extends String, ? extends String> m) {
        for (String key : m.keySet()) {
            put(key, m.get(key));
        }
    }

    public SortedSet<String> get(Object key) {
        return wrappedMap.get(key);
    }

    /**
     * Returns an application/x-www-form-encoded string of all values for the
     * given parameter.
     * 
     * @param key
     *        the parameter name
     * @return the form encoded value string
     */
    public String getFormEncoded(Object key) {
        StringBuilder sb = new StringBuilder();
        key = OAuth.percentEncode((String) key);
        Set<String> values = wrappedMap.get(key);
        if (values == null) {
            return key + "=";
        }
        Iterator<String> iter = values.iterator();
        while (iter.hasNext()) {
            sb.append(key + "=" + iter.next());
            if (iter.hasNext()) {
                sb.append("&");
            }
        }
        return sb.toString();
    }

    public boolean containsKey(Object key) {
        return wrappedMap.containsKey(key);
    }

    public boolean containsValue(Object value) {
        for (Set<String> values : wrappedMap.values()) {
            if (values.contains(value)) {
                return true;
            }
        }
        return false;
    }

    public int size() {
        int count = 0;
        for (String key : wrappedMap.keySet()) {
            count += wrappedMap.get(key).size();
        }
        return count;
    }

    public boolean isEmpty() {
        return wrappedMap.isEmpty();
    }

    public void clear() {
        wrappedMap.clear();
    }

    public SortedSet<String> remove(Object key) {
        return wrappedMap.remove(key);
    }

    public Set<String> keySet() {
        return wrappedMap.keySet();
    }

    public Collection<SortedSet<String>> values() {
        return wrappedMap.values();
    }

    public Set<java.util.Map.Entry<String, SortedSet<String>>> entrySet() {
        return wrappedMap.entrySet();
    }
}
