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
package oauth.signpost.commonshttp5.async;

import oauth.signpost.AbstractOAuthProvider;
import oauth.signpost.http.HttpRequest;
import oauth.signpost.http.HttpResponse;
import org.apache.hc.client5.http.async.methods.SimpleHttpRequest;
import org.apache.hc.client5.http.async.methods.SimpleHttpResponse;
import org.apache.hc.client5.http.async.methods.SimpleRequestProducer;
import org.apache.hc.client5.http.async.methods.SimpleResponseConsumer;
import org.apache.hc.client5.http.impl.async.CloseableHttpAsyncClient;
import org.apache.hc.client5.http.impl.async.HttpAsyncClients;
import org.apache.hc.client5.http.protocol.HttpClientContext;
import org.apache.hc.core5.concurrent.FutureCallback;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.Method;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.message.BasicHttpResponse;

import java.io.IOException;
import java.net.URI;
import java.util.concurrent.Future;

/**
 * This implementation uses the Apache Commons {@link org.apache.hc.client5.http.async.HttpAsyncClient} 5.x HTTP
 * implementation to fetch OAuth tokens from a service provider.
 *
 * @author Kristof Jozsa
 */
public class CommonsHttpAsyncOAuthProvider extends AbstractOAuthProvider {

    private static final long serialVersionUID = 1L;

    private transient CloseableHttpAsyncClient httpClient;

    public CommonsHttpAsyncOAuthProvider(String requestTokenEndpointUrl, String accessTokenEndpointUrl,
                                         String authorizationWebsiteUrl) {
        super(requestTokenEndpointUrl, accessTokenEndpointUrl, authorizationWebsiteUrl);
        this.httpClient = HttpAsyncClients.createDefault();
    }

    public CommonsHttpAsyncOAuthProvider(String requestTokenEndpointUrl, String accessTokenEndpointUrl,
                                         String authorizationWebsiteUrl, CloseableHttpAsyncClient httpClient) {
        super(requestTokenEndpointUrl, accessTokenEndpointUrl, authorizationWebsiteUrl);
        this.httpClient = httpClient;
    }

    public void setHttpClient(CloseableHttpAsyncClient httpClient) {
        this.httpClient = httpClient;
    }

    @Override
    protected HttpRequest createRequest(String endpointUrl) throws Exception {
        return new HttpAsyncRequestAdapter(new SimpleHttpRequest(Method.POST, new URI(endpointUrl)));
    }

    @Override
    protected HttpResponse sendRequest(HttpRequest request) throws Exception {
        SimpleRequestProducer requestProducer = SimpleRequestProducer.create((SimpleHttpRequest) request.unwrap());
        Future<SimpleHttpResponse> response = httpClient.execute(requestProducer, SimpleResponseConsumer.create(), HttpClientContext.create(), new FutureCallback<SimpleHttpResponse>() {
            @Override
            public void completed(SimpleHttpResponse simpleHttpResponse) {
            }

            @Override
            public void failed(Exception e) {
                throw new RuntimeException(e);
            }

            @Override
            public void cancelled() {
            }
        });
        return new HttpAsyncResponseAdapter(response.get());
    }

    @Override
    protected void closeConnection(HttpRequest request, oauth.signpost.http.HttpResponse response) throws Exception {
        httpClient.close();
    }
}
