package oauth.signpost.http;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.naming.OperationNotSupportedException;

import oauth.signpost.OAuth;

public class RequestParameters implements Map<String, String> {

    private static final long serialVersionUID = -2281503352590395824L;

    private TreeMap<String, Set<String>> wrappedMap = new TreeMap<String, Set<String>>();

    public String put(String key, String value) {
        Set<String> values = wrappedMap.get(key);
        if (values == null) {
            values = new TreeSet<String>();
            wrappedMap.put(OAuth.percentEncode(key), values);
        }
        value = OAuth.percentEncode(value);
        values.add(value);

        return value;
    }

    public void putAll(Map<? extends String, ? extends String> m) {
        for (String key : m.keySet()) {
            put(key, m.get(key));
        }
    }

    public String get(Object key) {
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
        return wrappedMap.size();
    }

    public boolean isEmpty() {
        return wrappedMap.isEmpty();
    }

    public void clear() {
        wrappedMap.clear();
    }

    public String remove(Object key) {
        wrappedMap.remove(key);
        return null;
    }

    public Set<String> keySet() {
        return wrappedMap.keySet();
    }

    public Collection<String> values() {
        LinkedList<String> values = new LinkedList<String>();
        for (Set<String> v : wrappedMap.values()) {
            values.addAll(v);
        }
        return values;
    }

    public Set<java.util.Map.Entry<String, String>> entrySet() {
        throw new RuntimeException(new OperationNotSupportedException());
    }
}
