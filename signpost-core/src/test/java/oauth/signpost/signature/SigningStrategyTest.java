package oauth.signpost.signature;

import static org.junit.Assert.assertEquals;
import oauth.signpost.SignpostTestBase;
import oauth.signpost.http.HttpParameters;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnit44Runner;

@RunWith(MockitoJUnit44Runner.class)
public class SigningStrategyTest extends SignpostTestBase {

    @Test
    public void testDifferentSigningStrategies() throws Exception {
        SigningStrategy strategy = null;
        String signature = "123";
        HttpParameters params = new HttpParameters();
        params.put("realm", "http://x.com");
        params.put("oauth_token", "abc");
        params.put("x_oauth_custom_param", "cde");
        params.put("should_not_appear", "nono");

        strategy = new AuthorizationHeaderSigningStrategy();
        assertEquals(
            "OAuth realm=\"http://x.com\", oauth_signature=\"123\", oauth_token=\"abc\", x_oauth_custom_param=\"cde\"",
            strategy.writeSignature(signature, httpGetMock, params));

        strategy = new QueryStringSigningStrategy();
        assertEquals(
            "http://www.example.com?oauth_signature=123&oauth_token=abc&x_oauth_custom_param=cde",
            strategy.writeSignature(signature, httpGetMock, params));

    }
}
