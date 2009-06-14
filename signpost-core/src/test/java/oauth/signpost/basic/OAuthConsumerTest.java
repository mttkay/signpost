package oauth.signpost.basic;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;

import java.util.HashMap;

import oauth.signpost.OAuthConsumer;
import oauth.signpost.SignpostTestBase;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.signature.SignatureMethod;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatcher;
import org.mockito.runners.MockitoJUnit44Runner;

@RunWith(MockitoJUnit44Runner.class)
public class OAuthConsumerTest extends SignpostTestBase {

    OAuthConsumer consumer = new DefaultOAuthConsumer(CONSUMER_KEY,
            CONSUMER_SECRET, SignatureMethod.HMAC_SHA1);

    @Test(expected = OAuthExpectationFailedException.class)
    public void shouldThrowIfConsumerKeyNotSet() throws Exception {
        OAuthConsumer consumer = new DefaultOAuthConsumer(null,
                CONSUMER_SECRET, SignatureMethod.HMAC_SHA1);
        consumer.setTokenWithSecret(TOKEN, TOKEN_SECRET);
        consumer.sign(httpGetMock);
    }

    @Test(expected = OAuthExpectationFailedException.class)
    public void shouldThrowIfConsumerSecretNotSet() throws Exception {
        OAuthConsumer consumer = new DefaultOAuthConsumer(CONSUMER_KEY, null,
                SignatureMethod.HMAC_SHA1);
        consumer.setTokenWithSecret(TOKEN, TOKEN_SECRET);
        consumer.sign(httpGetMock);
    }

    @Test
    public void shouldSignHttpRequestMessage() throws Exception {

        OAuthConsumer consumer = new DefaultOAuthConsumer(CONSUMER_KEY,
                CONSUMER_SECRET, SignatureMethod.HMAC_SHA1);

        consumer.setTokenWithSecret(TOKEN, TOKEN_SECRET);

        consumer.sign(httpGetMock);

        verify(httpGetMock).setHeader(eq("Authorization"),
                argThat(new IsCompleteListOfOAuthParameters()));
    }

    @Test
    public void shouldPercentEncodeOAuthParameters() throws Exception {
        OAuthConsumer consumer = new DefaultOAuthConsumer("1%2",
                CONSUMER_SECRET, SignatureMethod.HMAC_SHA1);
        consumer.setTokenWithSecret("3 4", TOKEN_SECRET);

        consumer.sign(httpGetMock);

        verify(httpGetMock).setHeader(eq("Authorization"),
                argThat(new HasValuesPercentEncoded()));
    }

    private class IsCompleteListOfOAuthParameters extends
            ArgumentMatcher<String> {

        @Override
        public boolean matches(Object argument) {
            String oauthHeader = (String) argument;
            assertTrue(oauthHeader.startsWith("OAuth "));
            HashMap<String, String> params = oauthHeaderToParamsMap(oauthHeader);
            assertNotNull(params.get("oauth_consumer_key"));
            assertNotNull(params.get("oauth_token"));
            assertNotNull(params.get("oauth_signature_method"));
            assertNotNull(params.get("oauth_signature"));
            assertNotNull(params.get("oauth_timestamp"));
            assertNotNull(params.get("oauth_nonce"));
            assertNotNull(params.get("oauth_version"));
            return true;
        }
    }

    private class HasValuesPercentEncoded extends ArgumentMatcher<String> {

        @Override
        public boolean matches(Object argument) {
            String oauthHeader = (String) argument;
            HashMap<String, String> params = oauthHeaderToParamsMap(oauthHeader);
            assertEquals("\"1%252\"", params.get("oauth_consumer_key"));
            assertEquals("\"3%204\"", params.get("oauth_token"));
            return true;
        }
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
