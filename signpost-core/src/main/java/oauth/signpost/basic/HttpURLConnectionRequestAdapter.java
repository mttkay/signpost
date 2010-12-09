package oauth.signpost.basic;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import oauth.signpost.http.HttpRequest;

public class HttpURLConnectionRequestAdapter implements HttpRequest {

    protected HttpURLConnection connection;

    protected Map<String, String> excludedHeaderMap;
    
	/**
	 * A workaround that sun.net.www.protocol.http.HttpURLConnection does not
	 * return values for the following http request headers due to security
	 * reasons.
	 * 
	 * Refer to http://stackoverflow.com/questions/2864062/
	 * getrequestpropertyauthorization-always-returns-null
	 */
    private static final String[] EXCLUDE_HEADERS = {
        "Proxy-Authorization",
        "Authorization"
    };
    
    public HttpURLConnectionRequestAdapter(HttpURLConnection connection) {
        this.connection = connection;
    }

    public String getMethod() {
        return connection.getRequestMethod();
    }

    public String getRequestUrl() {
        return connection.getURL().toExternalForm();
    }

    public void setRequestUrl(String url) {
        // can't do
    }

    public void setHeader(String name, String value) {
        connection.setRequestProperty(name, value);
        if (isHeaderExcluded(name)) {
        	setExcludedHeader(name, value);
        }
    }

    public String getHeader(String name) {
        String value = connection.getRequestProperty(name);
        if (value == null) {
        	value = getExcludedHeader(name);
        } 
        
        return value;
    }

    public Map<String, String> getAllHeaders() {
        Map<String, List<String>> origHeaders = connection.getRequestProperties();
        Map<String, String> headers = new HashMap<String, String>(origHeaders.size());
        for (String name : origHeaders.keySet()) {
            List<String> values = origHeaders.get(name);
            if (!values.isEmpty()) {
                headers.put(name, values.get(0));
            }
        }
        
        if (excludedHeaderMap != null) {
        	Set<Entry<String,String>> entrySet = excludedHeaderMap.entrySet();
        	for (Entry<String, String> entry : entrySet) {
				if (headers.get(entry.getKey()) == null) {
					headers.put(entry.getKey(), entry.getValue());
				}
			}
        }
        return headers;
    }

    public InputStream getMessagePayload() throws IOException {
        return null;
    }

    public String getContentType() {
        return connection.getRequestProperty("Content-Type");
    }

    public HttpURLConnection unwrap() {
        return connection;
    }
    
    private void setExcludedHeader(String name, String value) {
    	assert name != null : "Header name cannot be null";
    	if (excludedHeaderMap == null) {
    		excludedHeaderMap = new HashMap<String, String>();
    	}
    	excludedHeaderMap.put(name, value);
    }
    
    private String getExcludedHeader(String name) {
    	assert name != null : "Header name cannot be null";
    	if (excludedHeaderMap == null) {
    		return null;
    	}
    	
    	return excludedHeaderMap.get(name);
    }
    
    private boolean isHeaderExcluded(String name) {
    	for (String header : EXCLUDE_HEADERS) {
			if (header.equalsIgnoreCase(name)) {
				return true;
			}
		}
    	
    	return false;
    }
}
