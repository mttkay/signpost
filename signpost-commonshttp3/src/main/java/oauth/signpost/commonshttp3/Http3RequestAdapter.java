package oauth.signpost.commonshttp3;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.URIException;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;


public class Http3RequestAdapter implements oauth.signpost.http.HttpRequest {
	private HttpMethod httpMethod;

    public Http3RequestAdapter(HttpMethod method) {
		this.httpMethod = method;
    }

    public String getMethod() {
		return httpMethod.getName();
    }

    public String getRequestUrl() {
		try {
			return httpMethod.getURI().toString();
		} catch (URIException ex) {
			throw new IllegalStateException(ex);
		}
    }

    public void setRequestUrl(String url) {
        throw new RuntimeException(new UnsupportedOperationException());
    }

    public String getHeader(String name) {
        Header header = httpMethod.getRequestHeader(name);
        if (header == null) {
            return null;
        }
        return header.getValue();
    }

    public void setHeader(String name, String value) {
        httpMethod.setRequestHeader(name, value);
    }

    public Map<String, String> getAllHeaders() {
        Header[] origHeaders = httpMethod.getRequestHeaders();
        HashMap<String, String> headers = new HashMap<String, String>();
        for (Header h : origHeaders) {
            headers.put(h.getName(), h.getValue());
        }
        return headers;
    }

    public String getContentType() {
		String type = null;
		if(httpMethod instanceof PostMethod){
            PostMethod postMethod = (PostMethod) httpMethod;			
			type = postMethod.getRequestEntity().getContentType();
		}else{
			Header header = httpMethod.getRequestHeader("Content-Type");
            type =   (header == null) ? null : header.getValue();
		}
		return type;
    }

    public InputStream getMessagePayload() throws IOException {
		InputStream body = null;
		if(httpMethod instanceof PostMethod){
            PostMethod postMethod = (PostMethod) httpMethod;			
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			postMethod.getRequestEntity().writeRequest(out);
			body = new ByteArrayInputStream(out.toByteArray());
		}else{
			throw new IllegalStateException("HTTP method " + httpMethod.getName() + " not supported");
		}

		return body;
    }

    public Object unwrap() {
        return httpMethod;
    }

}
