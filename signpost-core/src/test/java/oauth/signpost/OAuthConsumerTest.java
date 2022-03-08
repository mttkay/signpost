package oauth.signpost;

import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.http.HttpParameters;
import oauth.signpost.http.HttpRequest;
import oauth.signpost.signature.HmacSha1MessageSigner;
import oauth.signpost.signature.OAuthMessageSigner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatcher;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
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
    public void shouldSignUrl() throws Exception {

        OAuthConsumer consumer = buildConsumer(CONSUMER_KEY, CONSUMER_SECRET, null);

        consumer.setTokenWithSecret(TOKEN, TOKEN_SECRET);

        String result = consumer.sign("http://www.example.com?q=1");
        assertNotNull(result);

        String[] parts = result.split("\\?");
        assertEquals("parameters are missing", 2, parts.length);
        assertEquals("http://www.example.com", parts[0]);

        HttpParameters params = OAuth.decodeForm(parts[1]);
        assertAllOAuthParametersExist(params);

        assertEquals("1", params.getFirst("q"));
    }

    @Test
    public void shouldIncludeOAuthAndQueryAndBodyParams() throws Exception {

        // mock a request that has custom query, body, and header params set
        HttpRequest request = mock(HttpRequest.class);
        when(request.getRequestUrl()).thenReturn("http://example.com?a=1+1");
        ByteArrayInputStream body = new ByteArrayInputStream("b=2+2".getBytes());
        when(request.getMessagePayload()).thenReturn(body);
        when(request.getContentType()).thenReturn(
                "application/x-www-form-urlencoded; charset=ISO-8859-1");
        when(request.getHeader("Authorization")).thenReturn(
                "OAuth realm=\"http%3A%2F%2Fexample.com\", oauth_token=\"12%25345\", oauth_signature=\"1234\"");

        OAuthMessageSigner signer = mock(HmacSha1MessageSigner.class);
        consumer.setMessageSigner(signer);

        consumer.sign(request);

        // verify that all custom params are properly read and passed to the
        // message signer
        ArgumentMatcher<HttpParameters> hasAllParameters = params -> {
            assertEquals("1 1", params.getFirst("a", true));
            assertEquals("2 2", params.getFirst("b", true));
            assertEquals("http://example.com", params.getFirst("realm", true));
            assertEquals("12%345", params.getFirst("oauth_token", true));
            // signature should be dropped, not valid to pre-set
            assertNull(params.getFirst("oauth_signature"));
            return true;
        };

        verify(signer).sign(same(request), argThat(hasAllParameters));
    }

    @Test
    public void shouldHonorManuallySetSigningParameters() throws Exception {

        // mock a request that has custom query, body, and header params set
        HttpRequest request = mock(HttpRequest.class);
        when(request.getRequestUrl()).thenReturn("http://example.com?a=1");

        OAuthMessageSigner signer = mock(HmacSha1MessageSigner.class);
        consumer.setMessageSigner(signer);

        HttpParameters params = new HttpParameters();
        params.put("oauth_callback", "http://mycallback");
        consumer.setAdditionalParameters(params);

        consumer.sign(request);

        // verify that all custom params are properly read and passed to the
        // message signer
        ArgumentMatcher<HttpParameters> hasParameters = params1 -> {
            assertEquals("http://mycallback", params1.getFirst("oauth_callback"));
            assertEquals("1", params1.getFirst("a"));
            return true;
        };

        verify(signer).sign(same(request), argThat(hasParameters));
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

        ObjectInputStream istream = new ObjectInputStream(new ByteArrayInputStream(
                baos.toByteArray()));
        consumer = (OAuthConsumer) istream.readObject();

        assertEquals(CONSUMER_KEY, consumer.getConsumerKey());
        assertEquals(CONSUMER_SECRET, consumer.getConsumerSecret());
        assertEquals(TOKEN, consumer.getToken());
        assertEquals(TOKEN_SECRET, consumer.getTokenSecret());

        // signing messages should still work
        consumer.sign(httpGetMock);
    }

    @Test
    public void shoudNotCllideOnMultiThread() throws Exception {
        for (int i = 0; i < 10; i++) {
            initRequestMocks();

            final OAuthConsumer consumer = buildConsumer(CONSUMER_KEY, CONSUMER_SECRET, null);
            consumer.setTokenWithSecret(TOKEN, TOKEN_SECRET);
            Thread t1 = new Thread() {

                @Override
                public void run() {
                    try {
                        consumer.sign(httpGetMock);
                    } catch (Exception e) {
                        throw new RuntimeException("sign error", e);
                    }
                }
            };
            Thread t2 = new Thread() {

                @Override
                public void run() {
                    try {
                        consumer.sign(httpGetMockWithQueryString);
                    } catch (Exception e) {
                        throw new RuntimeException("sign error", e);
                    }
                }
            };
            t1.start();
            t2.start();
            t1.join();
            t2.join();

            ArgumentCaptor<String> arg1 = ArgumentCaptor.forClass(String.class);
            ArgumentCaptor<String> arg2 = ArgumentCaptor.forClass(String.class);

            verify(httpGetMock).setHeader(eq("Authorization"), arg1.capture());
            verify(httpGetMockWithQueryString).setHeader(eq("Authorization"), arg2.capture());
            HttpParameters headerMap1 = OAuth.oauthHeaderToParamsMap(arg1.getValue());
            HttpParameters headerMap2 = OAuth.oauthHeaderToParamsMap(arg2.getValue());
            assertThat(headerMap1.getFirst(OAuth.OAUTH_NONCE), not(equalTo(headerMap2.getFirst(OAuth.OAUTH_NONCE))));
        }
    }
    // @Test
    // public void shouldSupport2LeggedOAuth() throws Exception {
    // OAuthConsumer consumer = buildConsumer(CONSUMER_KEY, CONSUMER_SECRET,
    // null);
    //
    // // note how we do not set a token and secret; should still include the
    // // oauth_token parameter
    //
    // consumer.sign(httpGetMock);
    //
    // verify(httpGetMock).setHeader(eq("Authorization"),
    // argThat(new IsCompleteListOfOAuthParameters()));
    // }

    private class IsCompleteListOfOAuthParameters implements ArgumentMatcher<String> {

        @Override
        public boolean matches(String oauthHeader) {
            assertTrue(oauthHeader.startsWith("OAuth "));
            assertAllOAuthParametersExist(OAuth.oauthHeaderToParamsMap(oauthHeader));
            return true;
        }
    }

    private void assertAllOAuthParametersExist(HttpParameters params) {
        assertNotNull(params.getFirst("oauth_consumer_key"));
        assertNotNull(params.getFirst("oauth_token"));
        assertNotNull(params.getFirst("oauth_signature_method"));
        assertNotNull(params.getFirst("oauth_signature"));
        assertNotNull(params.getFirst("oauth_timestamp"));
        assertNotNull(params.getFirst("oauth_nonce"));
        assertNotNull(params.getFirst("oauth_version"));
    }

    private class HasValuesPercentEncoded implements ArgumentMatcher<String> {

        @Override
        public boolean matches(String argument) {
            String oauthHeader = (String) argument;
            HttpParameters params = OAuth.oauthHeaderToParamsMap(oauthHeader);
            assertEquals("1%252", params.getFirst("oauth_consumer_key"));
            assertEquals("3%204", params.getFirst("oauth_token"));
            return true;
        }
    }
}
