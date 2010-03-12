package oauth.signpost.basic;

import oauth.signpost.OAuth;
import oauth.signpost.OAuthProvider;
import oauth.signpost.OAuthProviderTest;
import oauth.signpost.mocks.DefaultOAuthProviderMock;

import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnit44Runner;

@RunWith(MockitoJUnit44Runner.class)
public class DefaultOAuthProviderTest extends OAuthProviderTest {

    protected OAuthProvider buildProvider(String requestTokenUrl, String accessTokenUrl,
            String websiteUrl, boolean mockConnection) throws Exception {
        if (mockConnection) {
            DefaultOAuthProviderMock provider = new DefaultOAuthProviderMock(requestTokenUrl,
                    accessTokenUrl, websiteUrl);
            provider.mockConnection(OAuth.OAUTH_TOKEN + "=" + TOKEN + "&"
                    + OAuth.OAUTH_TOKEN_SECRET + "=" + TOKEN_SECRET);
            return provider;
        }
        return new DefaultOAuthProvider(requestTokenUrl, accessTokenUrl, websiteUrl);
    }
}
