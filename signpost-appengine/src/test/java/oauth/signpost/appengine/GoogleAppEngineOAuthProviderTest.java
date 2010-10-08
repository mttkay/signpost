package oauth.signpost.appengine;

import oauth.signpost.OAuth;
import oauth.signpost.OAuthProvider;
import oauth.signpost.OAuthProviderTest;

import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnit44Runner;

@RunWith(MockitoJUnit44Runner.class)
public class GoogleAppEngineOAuthProviderTest extends OAuthProviderTest {

	@Override
	protected OAuthProvider buildProvider(String requestTokenUrl,
			String accessTokenUrl, String websiteUrl, boolean mockConnection)
			throws Exception {
		if (mockConnection) {
			GoogleAppEngineOAuthProviderMock provider = new GoogleAppEngineOAuthProviderMock(
					requestTokenUrl, accessTokenUrl, websiteUrl);
			provider.mockConnection(OAuth.OAUTH_TOKEN + "=" + TOKEN + "&"
					+ OAuth.OAUTH_TOKEN_SECRET + "=" + TOKEN_SECRET);
			return provider;
		}
		return new GoogleAppEngineOAuthProvider(requestTokenUrl,
				accessTokenUrl, websiteUrl);
	}
}
