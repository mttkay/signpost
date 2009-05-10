package oauth.signpost;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gdata.util.common.base.PercentEscaper;

public class OAuth {

    public static final String VERSION_1_0 = "1.0";

    public static final String ENCODING = "UTF-8";

    public static final String FORM_ENCODED = "application/x-www-form-urlencoded";

    public static final String HTTP_AUTHORIZATION_HEADER = "Authorization";

    public static final String OAUTH_CONSUMER_KEY = "oauth_consumer_key";

    public static final String OAUTH_TOKEN = "oauth_token";

    public static final String OAUTH_TOKEN_SECRET = "oauth_token_secret";

    public static final String OAUTH_SIGNATURE_METHOD = "oauth_signature_method";

    public static final String OAUTH_SIGNATURE = "oauth_signature";

    public static final String OAUTH_TIMESTAMP = "oauth_timestamp";

    public static final String OAUTH_NONCE = "oauth_nonce";

    public static final String OAUTH_VERSION = "oauth_version";

    public static final String OAUTH_CALLBACK = "oauth_callback";

    private static final PercentEscaper percentEncoder = new PercentEscaper(
            "-._~", false);

    public static String percentEncode(String s) {
        if (s == null) {
            return "";
        }
        return percentEncoder.escape(s);
    }

    public static String percentDecode(String s) {
        try {
            if (s == null) {
                return "";
            }
            return URLDecoder.decode(s, ENCODING);
            // This implements http://oauth.pbwiki.com/FlexibleDecoding
        } catch (java.io.UnsupportedEncodingException wow) {
            throw new RuntimeException(wow.getMessage(), wow);
        }
    }

    /**
     * Construct a x-www-form-urlencoded document containing the given sequence
     * of name/value pairs. Use OAuth percent encoding (not exactly the encoding
     * mandated by x-www-form-urlencoded).
     */
    public static void formEncode(Collection<Parameter> parameters,
            OutputStream into) throws IOException {
        if (parameters != null) {
            boolean first = true;
            for (Parameter parameter : parameters) {
                if (first) {
                    first = false;
                } else {
                    into.write('&');
                }
                into.write(percentEncode(toString(parameter.getKey())).getBytes());
                into.write('=');
                into.write(percentEncode(toString(parameter.getValue())).getBytes());
            }
        }
    }

    /**
     * Construct a x-www-form-urlencoded document containing the given sequence
     * of name/value pairs. Use OAuth percent encoding (not exactly the encoding
     * mandated by x-www-form-urlencoded).
     */
    public static String formEncode(Collection<Parameter> parameters)
            throws IOException {
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        formEncode(parameters, b);
        return new String(b.toByteArray());
    }

    /** Parse a form-urlencoded document. */
    public static List<Parameter> decodeForm(String form) {
        ArrayList<Parameter> params = new ArrayList<Parameter>();
        if (!isEmpty(form)) {
            for (String nvp : form.split("\\&")) {
                int equals = nvp.indexOf('=');
                String name;
                String value;
                if (equals < 0) {
                    name = percentDecode(nvp);
                    value = null;
                } else {
                    name = percentDecode(nvp.substring(0, equals));
                    value = percentDecode(nvp.substring(equals + 1));
                }

                params.add(new Parameter(name, value));
            }
        }
        return params;
    }

    public static List<Parameter> decodeForm(InputStream content)
            throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(
                content));
        StringBuilder sb = new StringBuilder();
        String line = reader.readLine();
        while (line != null) {
            sb.append(line);
            line = reader.readLine();
        }

        return decodeForm(sb.toString());
    }

    /**
     * Construct a Map containing a copy of the given parameters. If several
     * parameters have the same name, the Map will contain the first value,
     * only.
     */
    public static Map<String, String> toMap(Collection<Parameter> from) {
        HashMap<String, String> map = new HashMap<String, String>();
        if (from != null) {
            for (Parameter param : from) {
                String key = toString(param.getKey());
                if (!map.containsKey(key)) {
                    map.put(key, toString(param.getValue()));
                }
            }
        }
        return map;
    }

    private static final String toString(Object from) {
        return (from == null) ? null : from.toString();
    }

    private static boolean isEmpty(String str) {
        return (str == null) || (str.length() == 0);
    }
}
