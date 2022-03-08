package oauth.signpost.commonshttp5.async;

import oauth.signpost.OAuth;
import oauth.signpost.OAuthProvider;
import oauth.signpost.OAuthProviderTest;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class CommonsHttpAsyncOAuthProviderTest extends OAuthProviderTest {

    @Override
    protected OAuthProvider buildProvider(String requestTokenUrl, String accessTokenUrl,
                                          String websiteUrl, boolean mockConnection) throws Exception {
        if (mockConnection) {
            CommonHttpAsyncOAuthProviderMock provider = new CommonHttpAsyncOAuthProviderMock(requestTokenUrl, accessTokenUrl, websiteUrl);
            provider.mockConnection(OAuth.OAUTH_TOKEN + "=" + TOKEN + "&"
                    + OAuth.OAUTH_TOKEN_SECRET + "=" + TOKEN_SECRET);
            return provider;
        }
        return new CommonsHttpAsyncOAuthProvider(requestTokenUrl, accessTokenUrl, websiteUrl);
    }
}
