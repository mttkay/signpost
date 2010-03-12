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
package oauth.signpost.jetty;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import oauth.signpost.http.HttpRequest;

import org.mortbay.jetty.HttpFields;
import org.mortbay.jetty.HttpFields.Field;
import org.mortbay.jetty.client.HttpExchange;

public class HttpRequestAdapter implements HttpRequest {

    private HttpExchange request;

    private String requestUrl;

    public HttpRequestAdapter(HttpExchange request) {
        this.request = request;
        buildRequestUrl();
    }

    public String getContentType() {
        HttpFields fields = request.getRequestFields();
        return fields.getStringField("Content-Type");
    }

    public InputStream getMessagePayload() throws IOException {
        return new ByteArrayInputStream(request.getRequestContent().array());
    }

    public String getMethod() {
        return request.getMethod();
    }

    public String getRequestUrl() {
        return requestUrl;
    }

    public void setRequestUrl(String url) {
        throw new RuntimeException(new UnsupportedOperationException());
    }

    public void setHeader(String name, String value) {
        request.setRequestHeader(name, value);
    }

    public String getHeader(String name) {
        HttpFields fields = request.getRequestFields();
        return fields.getStringField(name);
    }

    @SuppressWarnings("unchecked")
    public Map<String, String> getAllHeaders() {
        HttpFields fields = request.getRequestFields();
        Iterator iter = fields.getFields();
        HashMap<String, String> headers = new HashMap<String, String>();
        while (iter.hasNext()) {
            Field field = (Field) iter.next();
            headers.put(field.getName(), field.getValue());
        }

        return headers;
    }

    // Jetty has some very weird mechanism for handling URLs... we have to
    // reconstruct it here.
    private void buildRequestUrl() {
        StringBuilder sb = new StringBuilder();
        sb.append(request.getScheme() + "://");
        sb.append(request.getAddress().toString().replaceAll(":\\d+", ""));
        if (request.getURI() != null) {
            // the "URI" in Jetty is actually the path... WTF?!
            sb.append(request.getURI());
        }
        this.requestUrl = sb.toString();
    }

    public Object unwrap() {
        return request;
    }
}
