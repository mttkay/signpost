package oauth.signpost.basic;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Map;

import oauth.signpost.OAuth;
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

    @Test
    public void shouldBeSerializable() throws Exception {
        OAuthConsumer consumer = new DefaultOAuthConsumer(CONSUMER_KEY,
                CONSUMER_SECRET, SignatureMethod.HMAC_SHA1);
        consumer.setTokenWithSecret(TOKEN, TOKEN_SECRET);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream ostream = new ObjectOutputStream(baos);
        ostream.writeObject(consumer);

        ObjectInputStream istream = new ObjectInputStream(
                new ByteArrayInputStream(baos.toByteArray()));
        consumer = (DefaultOAuthConsumer) istream.readObject();

        assertEquals(CONSUMER_KEY, consumer.getConsumerKey());
        assertEquals(CONSUMER_SECRET, consumer.getConsumerSecret());
        assertEquals(TOKEN, consumer.getToken());
        assertEquals(TOKEN_SECRET, consumer.getTokenSecret());

        // signing messages should still work
        consumer.sign(httpGetMock);
    }

    @Test
    public void shouldSupport2LeggedOAuth() throws Exception {
        OAuthConsumer consumer = new DefaultOAuthConsumer(CONSUMER_KEY,
                CONSUMER_SECRET, SignatureMethod.HMAC_SHA1);

        // note how we do not set a token and secret; should still include the
        // oauth_token parameter

        consumer.sign(httpGetMock);

        verify(httpGetMock).setHeader(eq("Authorization"),
                argThat(new IsCompleteListOfOAuthParameters()));
    }

    private class IsCompleteListOfOAuthParameters extends
            ArgumentMatcher<String> {

        @Override
        public boolean matches(Object argument) {
            String oauthHeader = (String) argument;
            assertTrue(oauthHeader.startsWith("OAuth "));
            Map<String, String> params = OAuth.oauthHeaderToParamsMap(oauthHeader);
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
            Map<String, String> params = OAuth.oauthHeaderToParamsMap(oauthHeader);
            assertEquals("\"1%252\"", params.get("oauth_consumer_key"));
            assertEquals("\"3%204\"", params.get("oauth_token"));
            return true;
        }
    }
}
