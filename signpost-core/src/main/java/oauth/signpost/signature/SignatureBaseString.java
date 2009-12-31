/*
 * Copyright (c) 2009 Matthias Kaeppler Licensed under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law
 * or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package oauth.signpost.signature;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;

import oauth.signpost.OAuth;
import oauth.signpost.exception.OAuthMessageSignerException;
import oauth.signpost.http.HttpRequest;

public class SignatureBaseString {

    private HttpRequest request;

    private Map<String, String> requestParameters;

    /**
     * Wrapper for an OAuth key/value pair that is easily sortable.
     */
    private static class ComparableParameter implements Comparable<ComparableParameter>,
            Map.Entry<String, String> {

        private ComparableParameter(String key, String value) {
            this.key = OAuth.percentEncode(safeString(key));
            this.value = OAuth.percentEncode(safeString(value));
            this.combined = key + ' ' + value;
            // ' ' is used because it comes before any character
            // that can appear in a percentEncoded string.
        }

        private String key, value, combined;

        private static String safeString(String from) {
            return (from == null) ? null : from.toString();
        }

        public String getKey() {
            return this.key;
        }

        public String getValue() {
            return this.value;
        }

        public String setValue(String value) {
            this.value = value;
            return value;
        }

        public int compareTo(ComparableParameter that) {
            return this.combined.compareTo(that.combined);
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((key == null) ? 0 : key.hashCode());
            result = prime * result + ((value == null) ? 0 : value.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            ComparableParameter other = (ComparableParameter) obj;
            if (key == null) {
                if (other.key != null)
                    return false;
            } else if (!key.equals(other.key))
                return false;
            if (value == null) {
                if (other.value != null)
                    return false;
            } else if (!value.equals(other.value))
                return false;
            return true;
        }
    }

    /**
     * Constructs a new SBS instance that will operate on the given request
     * object and parameter set.
     * 
     * @param request
     *        the HTTP request
     * @param requestParameters
     *        the set of request parameters from the Authorization header, query
     *        string and form body
     */
    public SignatureBaseString(HttpRequest request, Map<String, String> requestParameters) {
        this.request = request;
        this.requestParameters = requestParameters;
    }

    /**
     * Builds the signature base string from the data this instance was
     * configured with.
     * 
     * @return the signature base string
     * @throws OAuthMessageSignerException
     */
    public String generate() throws OAuthMessageSignerException {

        try {
            String normalizedUrl = normalizeRequestUrl();
            String normalizedParams = normalizeRequestParameters();

            return request.getMethod() + '&' + OAuth.percentEncode(normalizedUrl) + '&'
                    + OAuth.percentEncode(normalizedParams);
        } catch (Exception e) {
            throw new OAuthMessageSignerException(e);
        }
    }

    public String normalizeRequestUrl() throws URISyntaxException {
        URI uri = new URI(request.getRequestUrl());
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

    /**
     * Normalizes the set of request parameters this instance was configured
     * with, as per OAuth spec section 9.1.1. cf. {@link http
     * ://oauth.net/core/1.0a/#anchor13}
     * 
     * @param parameters
     *        the set of request parameters
     * @return the normalized params string
     * @throws IOException
     */
    public String normalizeRequestParameters() throws IOException {
        if (requestParameters == null) {
            return "";
        }
        ArrayList<ComparableParameter> sortedParams = new ArrayList<ComparableParameter>(
            requestParameters.size());
        for (String key : requestParameters.keySet()) {
            // ignnore 'realm' and 'signature' params
            if ("realm".equals(key) || OAuth.OAUTH_SIGNATURE.equals(key)) {
                continue;
            }
            sortedParams.add(new ComparableParameter(key, requestParameters.get(key)));
        }
        Collections.sort(sortedParams);

        StringBuilder sb = new StringBuilder();
        Iterator<ComparableParameter> iter = sortedParams.iterator();
        while (iter.hasNext()) {
            ComparableParameter p = iter.next();
            sb.append(p.key + "=" + p.value);
            if (iter.hasNext()) {
                sb.append("&");
            }
        }
        return sb.toString();
    }
}
