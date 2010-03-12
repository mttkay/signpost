package oauth.signpost;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URL;

import oauth.signpost.basic.DefaultOAuthConsumer;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.http.HttpParameters;
import oauth.signpost.http.HttpRequest;
import oauth.signpost.mocks.OAuthProviderMock;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnit44Runner;

@RunWith(MockitoJUnit44Runner.class)
public abstract class OAuthProviderTest extends SignpostTestBase {

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

        provider = buildProvider(REQUEST_TOKEN_ENDPOINT_URL, ACCESS_TOKEN_ENDPOINT_URL,
            AUTHORIZE_WEBSITE_URL, true);
    }

    protected abstract OAuthProvider buildProvider(String requestTokenUrl, String accessTokenUrl,
            String websiteUrl, boolean mockConnection) throws Exception;

    @Test(expected = OAuthExpectationFailedException.class)
    public void shouldThrowExpectationFailedIfConsumerKeyNotSet() throws Exception {
        provider = buildProvider(REQUEST_TOKEN_ENDPOINT_URL, ACCESS_TOKEN_ENDPOINT_URL,
            AUTHORIZE_WEBSITE_URL, true);
        provider.retrieveRequestToken(new DefaultOAuthConsumer(null, CONSUMER_SECRET),
            REQUEST_TOKEN_ENDPOINT_URL);
    }

    @Test(expected = OAuthExpectationFailedException.class)
    public void shouldThrowExpectationFailedIfConsumerSecretNotSet() throws Exception {
        provider = buildProvider(REQUEST_TOKEN_ENDPOINT_URL, ACCESS_TOKEN_ENDPOINT_URL,
            AUTHORIZE_WEBSITE_URL, true);
        provider.retrieveRequestToken(new DefaultOAuthConsumer(CONSUMER_KEY, null),
            REQUEST_TOKEN_ENDPOINT_URL);
    }

    @Test
    public void shouldRetrieveRequestTokenAndUpdateConsumer() throws Exception {

        String callbackUrl = "http://www.example.com";
        String result = provider.retrieveRequestToken(consumerMock, callbackUrl);

        verify(consumerMock).sign((HttpRequest) anyObject());
        verify(consumerMock).setTokenWithSecret(TOKEN, TOKEN_SECRET);

        assertEquals(AUTHORIZE_WEBSITE_URL + "?" + OAuth.OAUTH_TOKEN + "=" + TOKEN + "&"
                + OAuth.OAUTH_CALLBACK + "=" + "http%3A%2F%2Fwww.example.com", result);
    }

    @Test
    public void shouldRespectCustomQueryParametersInAuthWebsiteUrl() throws Exception {
        provider = buildProvider(REQUEST_TOKEN_ENDPOINT_URL, ACCESS_TOKEN_ENDPOINT_URL,
            "http://provider.com/authorize?q=1", true);

        String callbackUrl = "http://www.example.com";
        // the URL ctor checks for URL validity
        URL url = new URL(provider.retrieveRequestToken(consumerMock, callbackUrl));
        assertTrue(url.getQuery().startsWith("q=1&oauth_token="));
    }

    @Test(expected = OAuthExpectationFailedException.class)
    public void shouldThrowWhenGettingAccessTokenAndRequestTokenNotSet() throws Exception {
        when(consumerMock.getToken()).thenReturn(null);
        provider.retrieveAccessToken(consumerMock, null);
    }

    @Test(expected = OAuthExpectationFailedException.class)
    public void shouldThrowWhenGettingAccessTokenAndRequestTokenSecretNotSet() throws Exception {
        when(consumerMock.getTokenSecret()).thenReturn(null);
        provider.retrieveAccessToken(consumerMock, null);
    }

    @Test
    public void shouldRetrieveAccessTokenAndUpdateConsumer() throws Exception {

        provider.retrieveAccessToken(consumerMock, null);

        verify(consumerMock).sign((HttpRequest) anyObject());
        verify(consumerMock).setTokenWithSecret(TOKEN, TOKEN_SECRET);
    }

    @Test
    public void shouldMakeSpecialResponseParametersAvailableToConsumer() throws Exception {

        assertTrue(provider.getResponseParameters().isEmpty());

        ((OAuthProviderMock) provider).mockConnection(OAuth.OAUTH_TOKEN + "=" + TOKEN + "&"
                + OAuth.OAUTH_TOKEN_SECRET + "=" + TOKEN_SECRET + "&a=1");
        provider.retrieveRequestToken(consumerMock, null);

        assertEquals(1, provider.getResponseParameters().size());
        assertTrue(provider.getResponseParameters().containsKey("a"));
        assertEquals("1", provider.getResponseParameters().getFirst("a"));

        ((OAuthProviderMock) provider).mockConnection(OAuth.OAUTH_TOKEN + "=" + TOKEN + "&"
                + OAuth.OAUTH_TOKEN_SECRET + "=" + TOKEN_SECRET + "&b=2&c=3");
        provider.retrieveAccessToken(consumerMock, null);

        assertEquals(2, provider.getResponseParameters().size());
        assertTrue(provider.getResponseParameters().containsKey("b"));
        assertTrue(provider.getResponseParameters().containsKey("c"));
        assertEquals("2", provider.getResponseParameters().getFirst("b"));
        assertEquals("3", provider.getResponseParameters().getFirst("c"));
    }

    @Test
    public void shouldBeSerializable() throws Exception {
        // the mock consumer isn't serializable, thus set a normal one
        OAuthConsumer consumer = new DefaultOAuthConsumer(CONSUMER_KEY, CONSUMER_SECRET);
        consumer.setTokenWithSecret(TOKEN, TOKEN_SECRET);

        provider = buildProvider(REQUEST_TOKEN_ENDPOINT_URL, ACCESS_TOKEN_ENDPOINT_URL,
            AUTHORIZE_WEBSITE_URL, false);
        provider.setOAuth10a(true);

        // prepare a provider that has response params set
        HttpParameters params = new HttpParameters();
        params.put("a", "1");
        ((AbstractOAuthProvider) provider).setResponseParameters(params);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream ostream = new ObjectOutputStream(baos);
        ostream.writeObject(provider);

        ObjectInputStream istream = new ObjectInputStream(new ByteArrayInputStream(
                baos.toByteArray()));
        provider = (OAuthProvider) istream.readObject();

        assertEquals(REQUEST_TOKEN_ENDPOINT_URL, provider.getRequestTokenEndpointUrl());
        assertEquals(ACCESS_TOKEN_ENDPOINT_URL, provider.getAccessTokenEndpointUrl());
        assertEquals(AUTHORIZE_WEBSITE_URL, provider.getAuthorizationWebsiteUrl());
        assertEquals(true, provider.isOAuth10a());
        assertNotNull(provider.getResponseParameters());
        assertEquals("1", provider.getResponseParameters().getFirst("a"));
    }
}
