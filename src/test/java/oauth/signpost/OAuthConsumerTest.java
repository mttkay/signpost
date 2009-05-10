package oauth.signpost;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;

import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.impl.DefaultOAuthConsumer;
import oauth.signpost.signature.SignatureMethod;

import org.apache.http.Header;
import org.apache.http.client.methods.HttpGet;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnit44Runner;

@RunWith(MockitoJUnit44Runner.class)
public class OAuthConsumerTest extends SignpostTestBase {

    OAuthConsumer consumer = new DefaultOAuthConsumer(CONSUMER_KEY,
            CONSUMER_SECRET, SignatureMethod.HMAC_SHA1);

    HttpGet request = new HttpGet("http://example.com");

    @Test(expected = OAuthExpectationFailedException.class)
    public void shouldThrowIfConsumerKeyNotSet() throws Exception {
        OAuthConsumer consumer = new DefaultOAuthConsumer(null,
                CONSUMER_SECRET, SignatureMethod.HMAC_SHA1);
        consumer.setTokenWithSecret(TOKEN, TOKEN_SECRET);
        consumer.sign(request);
    }

    @Test(expected = OAuthExpectationFailedException.class)
    public void shouldThrowIfConsumerSecretNotSet() throws Exception {
        OAuthConsumer consumer = new DefaultOAuthConsumer(CONSUMER_KEY, null,
                SignatureMethod.HMAC_SHA1);
        consumer.setTokenWithSecret(TOKEN, TOKEN_SECRET);
        consumer.sign(request);
    }

    @Test
    public void shouldSignHttpRequestMessage() throws Exception {

        OAuthConsumer consumer = new DefaultOAuthConsumer(CONSUMER_KEY,
                CONSUMER_SECRET, SignatureMethod.HMAC_SHA1);

        consumer.setTokenWithSecret(TOKEN, TOKEN_SECRET);

        consumer.sign(request);

        Header authHeader = request.getFirstHeader("Authorization");
        assertNotNull(authHeader);

        String oauthHeader = authHeader.getValue();
        assertTrue(oauthHeader.startsWith("OAuth "));

        HashMap<String, String> params = oauthHeaderToParamsMap(oauthHeader);
        assertNotNull(params.get("oauth_consumer_key"));
        assertNotNull(params.get("oauth_token"));
        assertNotNull(params.get("oauth_signature_method"));
        assertNotNull(params.get("oauth_signature"));
        assertNotNull(params.get("oauth_timestamp"));
        assertNotNull(params.get("oauth_nonce"));
        assertNotNull(params.get("oauth_version"));
    }

    @Test
    public void shouldPercentEncodeOAuthParameters() throws Exception {
        OAuthConsumer consumer = new DefaultOAuthConsumer("1%2",
                CONSUMER_SECRET, SignatureMethod.HMAC_SHA1);
        consumer.setTokenWithSecret("3 4", TOKEN_SECRET);

        consumer.sign(request);

        Header authHeader = request.getFirstHeader("Authorization");
        assertNotNull(authHeader);

        String oauthHeader = authHeader.getValue();

        HashMap<String, String> params = oauthHeaderToParamsMap(oauthHeader);
        assertEquals("\"1%252\"", params.get("oauth_consumer_key"));
        assertEquals("\"3%204\"", params.get("oauth_token"));
    }

    private HashMap<String, String> oauthHeaderToParamsMap(String oauthHeader) {
        oauthHeader = oauthHeader.substring("OAuth ".length());
        String[] elements = oauthHeader.split(",");
        HashMap<String, String> params = new HashMap<String, String>();
        for (String keyValuePair : elements) {
            String[] keyValue = keyValuePair.split("=");
            params.put(keyValue[0].trim(), keyValue[1].trim());
        }
        return params;
    }
}
