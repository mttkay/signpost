package oauth.signpost.signature;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import oauth.signpost.OAuth;
import oauth.signpost.Parameter;
import oauth.signpost.exception.OAuthMessageSignerException;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpRequest;

public class SignatureBaseString {

    private HttpRequest request;

    private Map<String, String> oauthParams;

    /** An efficiently sortable wrapper around a parameter. */
    private static class ComparableParameter implements
            Comparable<ComparableParameter> {

        ComparableParameter(Parameter value) {
            this.value = value;
            String n = safeString(value.getKey());
            String v = safeString(value.getValue());
            this.key = OAuth.percentEncode(n) + ' ' + OAuth.percentEncode(v);
            // ' ' is used because it comes before any character
            // that can appear in a percentEncoded string.
        }

        final Parameter value;

        private final String key;

        private static String safeString(String from) {
            return (from == null) ? null : from.toString();
        }

        public int compareTo(ComparableParameter that) {
            return this.key.compareTo(that.key);
        }

        @Override
        public String toString() {
            return key;
        }

    }

    public SignatureBaseString(HttpRequest request,
            Map<String, String> oauthParams) {
        this.request = request;
        this.oauthParams = oauthParams;
    }

    public String compute() throws OAuthMessageSignerException {

        try {
            List<Parameter> params = new ArrayList<Parameter>();
            collectHeaderParameters(params);
            collectBodyParameters(params);
            String requestUrl = collectQueryParameters(params);

            return OAuth.percentEncode(request.getRequestLine().getMethod())
                    + '&' + OAuth.percentEncode(normalizeUrl(requestUrl)) + '&'
                    + OAuth.percentEncode(normalizeParameters(params));
        } catch (Exception e) {
            throw new OAuthMessageSignerException(e);
        }
    }

    public String normalizeUrl(String url) throws URISyntaxException {
        URI uri = new URI(url);
        String scheme = uri.getScheme().toLowerCase();
        String authority = uri.getAuthority().toLowerCase();
        boolean dropPort = (scheme.equals("http") && uri.getPort() == 80)
                || (scheme.equals("https") && uri.getPort() == 443);
        if (dropPort) {
            // find the last : in the authority
            int index = authority.lastIndexOf(":");
            if (index >= 0) {
                authority = authority.substring(0, index);
            }
        }
        String path = uri.getRawPath();
        if (path == null || path.length() <= 0) {
            path = "/"; // conforms to RFC 2616 section 3.2.2
        }
        // we know that there is no query and no fragment here.
        return scheme + "://" + authority + path;
    }

    public String normalizeParameters(Collection<Parameter> parameters)
            throws IOException {
        if (parameters == null) {
            return "";
        }
        List<ComparableParameter> p = new ArrayList<ComparableParameter>(
                parameters.size());
        for (Parameter parameter : parameters) {
            if (!OAuth.OAUTH_SIGNATURE.equals(parameter.getKey())) {
                p.add(new ComparableParameter(parameter));
            }
        }
        Collections.sort(p);
        return OAuth.formEncode(getParameters(p));
    }

    /**
     * Collects OAuth Authorization header parameters as per OAuth Core 1.0 spec
     * section 9.1.1
     */
    private void collectHeaderParameters(Collection<Parameter> parameters) {
        for (String key : oauthParams.keySet()) {
            parameters.add(new Parameter(key, oauthParams.get(key)));
        }
    }

    /**
     * Collects x-www-form-urlencoded body parameters as per OAuth Core 1.0 spec
     * section 9.1.1
     */
    private void collectBodyParameters(Collection<Parameter> parameters)
            throws IOException {

        // collect x-www-form-urlencoded body params
        if (request instanceof HttpEntityEnclosingRequest) {
            HttpEntityEnclosingRequest r = (HttpEntityEnclosingRequest) request;
            HttpEntity entity = r.getEntity();
            if (entity != null) {
                Header contentTypeHeader = entity.getContentType();
                if (contentTypeHeader != null
                        && contentTypeHeader.getValue().equals(
                                OAuth.FORM_ENCODED)) {
                    parameters.addAll(OAuth.decodeForm(entity.getContent()));
                }
            }
        }
    }

    /**
     * Collects HTTP GET query string parameters as per OAuth Core 1.0 spec
     * section 9.1.1
     * 
     * @param request
     *            The HTTP request
     * @return the URL without the query parameters, if there were any
     */
    private String collectQueryParameters(Collection<Parameter> parameters) {

        String url = request.getRequestLine().getUri();
        int q = url.indexOf('?');
        if (q >= 0) {
            // Combine the URL query string with the other parameters:
            parameters.addAll(OAuth.decodeForm(url.substring(q + 1)));
            url = url.substring(0, q);
        }

        return url;
    }

    /** Retrieve the original parameters from a sorted collection. */
    private List<Parameter> getParameters(
            Collection<ComparableParameter> parameters) {
        if (parameters == null) {
            return null;
        }
        ArrayList<Parameter> list = new ArrayList<Parameter>(parameters.size());
        for (ComparableParameter parameter : parameters) {
            list.add(parameter.value);
        }
        return list;
    }
}
