package oauth.signpost.basic;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;

import oauth.signpost.OAuth;
import oauth.signpost.OAuthConsumer;
import oauth.signpost.OAuthProvider;
import oauth.signpost.OAuthProviderTest;

import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnit44Runner;

@RunWith(MockitoJUnit44Runner.class)
public abstract class DefaultOAuthProviderTest extends OAuthProviderTest {

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

        // ((DefaultOAuthProvider)
        // provider).setHttpUrlConnection(connectionMock);
    }
}
