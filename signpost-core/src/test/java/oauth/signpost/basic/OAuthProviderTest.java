package oauth.signpost.basic;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

import oauth.signpost.AbstractOAuthProvider;
import oauth.signpost.OAuth;
import oauth.signpost.OAuthConsumer;
import oauth.signpost.OAuthProvider;
import oauth.signpost.SignpostTestBase;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.http.HttpRequest;
import oauth.signpost.signature.SignatureMethod;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnit44Runner;

@RunWith(MockitoJUnit44Runner.class)
public class OAuthProviderTest extends SignpostTestBase {

    protected OAuthProvider provider;

    @Mock
    OAuthConsumer consumerMock;

    @Before
    public void prepare() throws Exception {

        MockitoAnnotations.initMocks(this);

        // init consumer mock
        when(consumerMock.getConsumerKey()).thenReturn(CONSUMER_KEY);
        when(consumerMock.getConsumerSecret()).thenReturn(CONSUMER_SECRET);
        when(consumerMock.getToken()).thenReturn(TOKEN);
        when(consumerMock.getTokenSecret()).thenReturn(TOKEN_SECRET);

        provider = buildProvider(consumerMock, REQUEST_TOKEN_ENDPOINT_URL,
            ACCESS_TOKEN_ENDPOINT_URL, AUTHORIZE_WEBSITE_URL, true);
    }

    protected OAuthProvider buildProvider(OAuthConsumer consumer, String requestTokenUrl,
            String accessTokenUrl, String websiteUrl, boolean mockConnection) throws Exception {
        OAuthProvider provider = new DefaultOAuthProvider(consumer, requestTokenUrl,
            accessTokenUrl, websiteUrl);
        if (mockConnection) {
            mockConnection(provider, OAuth.OAUTH_TOKEN + "=" + TOKEN + "&"
                    + OAuth.OAUTH_TOKEN_SECRET + "=" + TOKEN_SECRET);
        }
        return provider;
    }

    protected void mockConnection(OAuthProvider provider, String responseBody) throws Exception {

        HttpURLConnection connectionMock = mock(HttpURLConnection.class);
        InputStream is = new ByteArrayInputStream(responseBody.getBytes());
        when(connectionMock.getResponseCode()).thenReturn(200);
        when(connectionMock.getInputStream()).thenReturn(is);

        ((DefaultOAuthProvider) provider).setHttpUrlConnection(connectionMock);
    }

    @Test(expected = OAuthExpectationFailedException.class)
    public void shouldThrowExpectationFailedIfConsumerKeyNotSet()
            throws Exception {
        provider = buildProvider(new DefaultOAuthConsumer(null,
                CONSUMER_SECRET, SignatureMethod.HMAC_SHA1),
                REQUEST_TOKEN_ENDPOINT_URL, ACCESS_TOKEN_ENDPOINT_URL,
            AUTHORIZE_WEBSITE_URL, true);
        provider.retrieveRequestToken(REQUEST_TOKEN_ENDPOINT_URL);
    }

    @Test(expected = OAuthExpectationFailedException.class)
    public void shouldThrowExpectationFailedIfConsumerSecretNotSet()
            throws Exception {
        provider = buildProvider(new DefaultOAuthConsumer(
                CONSUMER_KEY, null, SignatureMethod.HMAC_SHA1),
                REQUEST_TOKEN_ENDPOINT_URL, ACCESS_TOKEN_ENDPOINT_URL,
            AUTHORIZE_WEBSITE_URL, true);
        provider.retrieveRequestToken(REQUEST_TOKEN_ENDPOINT_URL);
    }

    @Test
    public void shouldRetrieveRequestTokenAndUpdateConsumer() throws Exception {

        String callbackUrl = "http://www.example.com";
        String result = provider.retrieveRequestToken(callbackUrl);

        verify(consumerMock).sign((HttpRequest) anyObject());
        verify(consumerMock).setTokenWithSecret(TOKEN, TOKEN_SECRET);

        assertEquals(AUTHORIZE_WEBSITE_URL + "?" + OAuth.OAUTH_TOKEN + "="
                + TOKEN + "&" + OAuth.OAUTH_CALLBACK + "="
                + "http%3A%2F%2Fwww.example.com", result);
    }

    @Test
    public void shouldRespectCustomQueryParametersInAuthWebsiteUrl() throws Exception {
        provider = buildProvider(consumerMock, REQUEST_TOKEN_ENDPOINT_URL,
            ACCESS_TOKEN_ENDPOINT_URL, "http://provider.com/authorize?q=1", true);

        String callbackUrl = "http://www.example.com";
        // the URL ctor checks for URL validity
        URL url = new URL(provider.retrieveRequestToken(callbackUrl));
        assertTrue(url.getQuery().startsWith("q=1&oauth_token="));
    }

    @Test(expected = OAuthExpectationFailedException.class)
    public void shouldThrowWhenGettingAccessTokenAndRequestTokenNotSet()
            throws Exception {
        when(consumerMock.getToken()).thenReturn(null);
        provider.retrieveAccessToken(null);
    }

    @Test(expected = OAuthExpectationFailedException.class)
    public void shouldThrowWhenGettingAccessTokenAndRequestTokenSecretNotSet()
            throws Exception {
        when(consumerMock.getTokenSecret()).thenReturn(null);
        provider.retrieveAccessToken(null);
    }

    @Test
    public void shouldRetrieveAccessTokenAndUpdateConsumer() throws Exception {

        provider.retrieveAccessToken(null);

        verify(consumerMock).sign((HttpRequest) anyObject());
        verify(consumerMock).setTokenWithSecret(TOKEN, TOKEN_SECRET);
    }

    @Test
    public void shouldMakeSpecialResponseParametersAvailableToConsumer() throws Exception {

        assertTrue(provider.getResponseParameters().isEmpty());

        mockConnection(provider, OAuth.OAUTH_TOKEN + "=" + TOKEN + "&" + OAuth.OAUTH_TOKEN_SECRET
                + "=" + TOKEN_SECRET + "&a=1");
        provider.retrieveRequestToken(null);

        assertEquals(1, provider.getResponseParameters().size());
        assertTrue(provider.getResponseParameters().containsKey("a"));
        assertEquals("1", provider.getResponseParameters().get("a"));

        mockConnection(provider, OAuth.OAUTH_TOKEN + "=" + TOKEN + "&" + OAuth.OAUTH_TOKEN_SECRET
                + "=" + TOKEN_SECRET + "&b=2&c=3");
        provider.retrieveAccessToken(null);

        assertEquals(2, provider.getResponseParameters().size());
        assertTrue(provider.getResponseParameters().containsKey("b"));
        assertTrue(provider.getResponseParameters().containsKey("c"));
        assertEquals("2", provider.getResponseParameters().get("b"));
        assertEquals("3", provider.getResponseParameters().get("c"));
    }

    @Test
    public void shouldBeSerializable() throws Exception {
        // the mock consumer isn't serializable, thus set a normal one
        OAuthConsumer consumer = new DefaultOAuthConsumer(CONSUMER_KEY, CONSUMER_SECRET,
            SignatureMethod.HMAC_SHA1);
        consumer.setTokenWithSecret(TOKEN, TOKEN_SECRET);

        provider = buildProvider(consumer, REQUEST_TOKEN_ENDPOINT_URL, ACCESS_TOKEN_ENDPOINT_URL,
            AUTHORIZE_WEBSITE_URL, false);
        provider.setOAuth10a(true);

        // prepare a provider that has response params set
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("a", "1");
        ((AbstractOAuthProvider) provider).setResponseParameters(params);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream ostream = new ObjectOutputStream(baos);
        ostream.writeObject(provider);

        ObjectInputStream istream = new ObjectInputStream(new ByteArrayInputStream(baos
            .toByteArray()));
        provider = (OAuthProvider) istream.readObject();

        assertEquals(REQUEST_TOKEN_ENDPOINT_URL, provider.getRequestTokenEndpointUrl());
        assertEquals(ACCESS_TOKEN_ENDPOINT_URL, provider.getAccessTokenEndpointUrl());
        assertEquals(AUTHORIZE_WEBSITE_URL, provider.getAuthorizationWebsiteUrl());
        assertEquals(true, provider.isOAuth10a());
        assertNotNull(provider.getConsumer());
        assertNotNull(provider.getResponseParameters());
        assertEquals("1", provider.getResponseParameters().get("a"));
    }
}
