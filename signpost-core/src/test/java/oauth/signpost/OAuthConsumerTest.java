package oauth.signpost;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;

import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.http.HttpRequest;
import oauth.signpost.signature.OAuthMessageSigner;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatcher;
import org.mockito.runners.MockitoJUnit44Runner;

@RunWith(MockitoJUnit44Runner.class)
public abstract class OAuthConsumerTest extends SignpostTestBase {

    protected OAuthConsumer consumer;

    protected abstract OAuthConsumer buildConsumer(String consumerKey, String consumerSecret,
            OAuthMessageSigner messageSigner);

    @Test(expected = OAuthExpectationFailedException.class)
    public void shouldThrowIfConsumerKeyNotSet() throws Exception {
        OAuthConsumer consumer = buildConsumer(null, CONSUMER_SECRET, null);
        consumer.setTokenWithSecret(TOKEN, TOKEN_SECRET);
        consumer.sign(httpGetMock);
    }

    @Test(expected = OAuthExpectationFailedException.class)
    public void shouldThrowIfConsumerSecretNotSet() throws Exception {
        OAuthConsumer consumer = buildConsumer(CONSUMER_KEY, null, null);
        consumer.setTokenWithSecret(TOKEN, TOKEN_SECRET);
        consumer.sign(httpGetMock);
    }

    @Test
    public void shouldSignHttpRequestMessage() throws Exception {

        OAuthConsumer consumer = buildConsumer(CONSUMER_KEY, CONSUMER_SECRET, null);

        consumer.setTokenWithSecret(TOKEN, TOKEN_SECRET);

        consumer.sign(httpGetMock);

        verify(httpGetMock).setHeader(eq("Authorization"),
            argThat(new IsCompleteListOfOAuthParameters()));
    }

    @Test
    public void shouldIncludeOAuthAndQueryAndBodyParams() throws Exception {

        HashMap<String, String> params = new HashMap<String, String>();

        HttpRequest request = mock(HttpRequest.class);
        when(request.getRequestUrl()).thenReturn("http://example.com?a=1");
        ByteArrayInputStream body = new ByteArrayInputStream("b=2".getBytes());
        when(request.getMessagePayload()).thenReturn(body);
        when(request.getContentType()).thenReturn("application/x-www-form-urlencoded");
        when(request.getHeader("Authorization")).thenReturn(
            "OAuth realm=www.example.com, oauth_signature=12345");

        consumer.sign(httpGetMock);

        // TODO

        // assertTrue(result.contains("a%3D1"));
        // assertTrue(result.contains("b%3D2"));
        // assertTrue(result.contains("oauth_consumer_key%3D" + CONSUMER_KEY));
        // assertTrue(result.contains("oauth_signature_method%3D" +
        // SIGNATURE_METHOD));
        // assertTrue(result.contains("oauth_timestamp%3D" + TIMESTAMP));
        // assertTrue(result.contains("oauth_nonce%3D" + NONCE));
        // assertTrue(result.contains("oauth_version%3D" + OAUTH_VERSION));
        // assertTrue(result.contains("oauth_token%3D" + TOKEN));
        //
        // // should ignore signature and realm params
        // assertFalse(result.contains("oauth_signature%3D12345"));
        // assertFalse(result.contains("realm%3Dwww.example.com"));
        //
        // // should not include the body param if not x-www-form-urlencoded
        // when(request.getContentType()).thenReturn(null);
        // sbs = new SignatureBaseString(request);
        // assertFalse(sbs.generate().contains("b%3D2"));
    }

    @Test
    public void shouldPercentEncodeOAuthParameters() throws Exception {
        OAuthConsumer consumer = buildConsumer("1%2", CONSUMER_SECRET, null);
        consumer.setTokenWithSecret("3 4", TOKEN_SECRET);

        consumer.sign(httpGetMock);

        verify(httpGetMock).setHeader(eq("Authorization"), argThat(new HasValuesPercentEncoded()));
    }

    @Test
    public void shouldBeSerializable() throws Exception {
        OAuthConsumer consumer = buildConsumer(CONSUMER_KEY, CONSUMER_SECRET, null);
        consumer.setTokenWithSecret(TOKEN, TOKEN_SECRET);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream ostream = new ObjectOutputStream(baos);
        ostream.writeObject(consumer);

        ObjectInputStream istream = new ObjectInputStream(new ByteArrayInputStream(baos
            .toByteArray()));
        consumer = (OAuthConsumer) istream.readObject();

        assertEquals(CONSUMER_KEY, consumer.getConsumerKey());
        assertEquals(CONSUMER_SECRET, consumer.getConsumerSecret());
        assertEquals(TOKEN, consumer.getToken());
        assertEquals(TOKEN_SECRET, consumer.getTokenSecret());

        // signing messages should still work
        consumer.sign(httpGetMock);
    }

    @Test
    public void shouldSupport2LeggedOAuth() throws Exception {
        OAuthConsumer consumer = buildConsumer(CONSUMER_KEY, CONSUMER_SECRET, null);

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
