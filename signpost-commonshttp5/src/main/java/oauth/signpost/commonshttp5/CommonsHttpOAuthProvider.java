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
package oauth.signpost.commonshttp5;

import oauth.signpost.AbstractOAuthProvider;
import oauth.signpost.http.HttpRequest;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ClassicHttpRequest;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.io.entity.EntityUtils;

import java.io.IOException;

/**
 * This implementation uses the Apache Commons {@link org.apache.hc.client5.http.classic.HttpClient} 5.x HTTP
 * implementation to fetch OAuth tokens from a service provider. Android users
 * should use this provider implementation in favor of the default one, since
 * the latter is known to cause problems with Android's Apache Harmony
 * underpinnings.
 *
 * @author Matthias Kaeppler
 */
public class CommonsHttpOAuthProvider extends AbstractOAuthProvider {

    private static final long serialVersionUID = 1L;

    private transient CloseableHttpClient httpClient;

    public CommonsHttpOAuthProvider(String requestTokenEndpointUrl, String accessTokenEndpointUrl,
                                    String authorizationWebsiteUrl) {
        super(requestTokenEndpointUrl, accessTokenEndpointUrl, authorizationWebsiteUrl);
        this.httpClient = HttpClients.createDefault();
    }

    public CommonsHttpOAuthProvider(String requestTokenEndpointUrl, String accessTokenEndpointUrl,
                                    String authorizationWebsiteUrl, CloseableHttpClient httpClient) {
        super(requestTokenEndpointUrl, accessTokenEndpointUrl, authorizationWebsiteUrl);
        this.httpClient = httpClient;
    }

    public void setHttpClient(CloseableHttpClient httpClient) {
        this.httpClient = httpClient;
    }

    @Override
    protected HttpRequest createRequest(String endpointUrl) throws Exception {
        HttpPost request = new HttpPost(endpointUrl);
        return new HttpRequestAdapter(request);
    }

    @Override
    protected oauth.signpost.http.HttpResponse sendRequest(HttpRequest request) throws Exception {
        ClassicHttpResponse response = httpClient.execute((ClassicHttpRequest) request.unwrap());
        return new HttpResponseAdapter(response);
    }

    @Override
    protected void closeConnection(HttpRequest request, oauth.signpost.http.HttpResponse response)
            throws Exception {
        if (response != null) {
            HttpEntity entity = ((ClassicHttpResponse) response.unwrap()).getEntity();
            if (entity != null) {
                try {
                    // free the connection
                    EntityUtils.consume(entity);
                } catch (IOException e) {
                    // this means HTTP keep-alive is not possible
                    e.printStackTrace();
                }
            }
        }
    }
}
