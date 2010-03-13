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
import java.util.Iterator;

import oauth.signpost.OAuth;
import oauth.signpost.exception.OAuthMessageSignerException;
import oauth.signpost.http.HttpRequest;
import oauth.signpost.http.HttpParameters;

public class SignatureBaseString {

    private HttpRequest request;

    private HttpParameters requestParameters;

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
    public SignatureBaseString(HttpRequest request, HttpParameters requestParameters) {
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
     * with, as per OAuth spec section 9.1.1.
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

        StringBuilder sb = new StringBuilder();
        Iterator<String> iter = requestParameters.keySet().iterator();

        for (int i = 0; iter.hasNext(); i++) {
            String param = iter.next();

            if (OAuth.OAUTH_SIGNATURE.equals(param) || "realm".equals(param)) {
                continue;
            }

            if (i > 0) {
                sb.append("&");
            }

            sb.append(requestParameters.getAsQueryString(param));
        }
        return sb.toString();
    }
}
