package oauth.signpost;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.impl.DefaultOAuthConsumer;
import oauth.signpost.impl.DefaultOAuthProvider;
import oauth.signpost.signature.SignatureMethod;

import org.apache.http.client.methods.HttpGet;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnit44Runner;

@RunWith(MockitoJUnit44Runner.class)
public class OAuthProviderTest extends SignpostTestBase {

    OAuthProvider provider;

    @Before
    public void initDefaultProvider() {
        provider = new DefaultOAuthProvider(consumerMock,
                REQUEST_TOKEN_ENDPOINT_URL, ACCESS_TOKEN_ENDPOINT_URL,
                AUTHORIZE_WEBSITE_URL);
        provider.setHttpClient(httpClientMock);
    }

    @Test(expected = OAuthExpectationFailedException.class)
    public void shouldThrowExpectationFailedIfConsumerKeyNotSet()
            throws Exception {
        provider = new DefaultOAuthProvider(new DefaultOAuthConsumer(null,
                CONSUMER_SECRET, SignatureMethod.HMAC_SHA1),
                REQUEST_TOKEN_ENDPOINT_URL, ACCESS_TOKEN_ENDPOINT_URL,
                AUTHORIZE_WEBSITE_URL);
        provider.setHttpClient(httpClientMock);
        provider.retrieveRequestToken(REQUEST_TOKEN_ENDPOINT_URL);
    }

    @Test(expected = OAuthExpectationFailedException.class)
    public void shouldThrowExpectationFailedIfConsumerSecretNotSet()
            throws Exception {
        provider = new DefaultOAuthProvider(new DefaultOAuthConsumer(
                CONSUMER_KEY, null, SignatureMethod.HMAC_SHA1),
                REQUEST_TOKEN_ENDPOINT_URL, ACCESS_TOKEN_ENDPOINT_URL,
                AUTHORIZE_WEBSITE_URL);
        provider.setHttpClient(httpClientMock);
        provider.retrieveRequestToken(REQUEST_TOKEN_ENDPOINT_URL);
    }

    @Test
    public void shouldRetrieveRequestTokenAndUpdateConsumer() throws Exception {

        String callbackUrl = "http://www.example.com";
        String result = provider.retrieveRequestToken(callbackUrl);

        verify(consumerMock).sign((HttpGet) anyObject());
        verify(consumerMock).setTokenWithSecret(TOKEN, TOKEN_SECRET);

        assertEquals(AUTHORIZE_WEBSITE_URL + "?" + OAuth.OAUTH_TOKEN + "="
                + TOKEN + "&" + OAuth.OAUTH_CALLBACK + "="
                + "http%3A%2F%2Fwww.example.com", result);
    }

    @Test(expected = OAuthExpectationFailedException.class)
    public void shouldThrowWhenGettingAccessTokenAndRequestTokenNotSet()
            throws Exception {
        when(consumerMock.getToken()).thenReturn(null);
        provider.retrieveAccessToken();
    }

    @Test(expected = OAuthExpectationFailedException.class)
    public void shouldThrowWhenGettingAccessTokenAndRequestTokenSecretNotSet()
            throws Exception {
        when(consumerMock.getTokenSecret()).thenReturn(null);
        provider.retrieveAccessToken();
    }

    @Test
    public void shouldRetrieveAccessTokenAndUpdateConsumer() throws Exception {

        provider.retrieveAccessToken();

        verify(consumerMock).sign((HttpGet) anyObject());
        verify(consumerMock).setTokenWithSecret(TOKEN, TOKEN_SECRET);
    }

}
