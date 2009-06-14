package oauth.signpost.signature;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.HashMap;

import oauth.signpost.SignpostTestBase;
import oauth.signpost.http.HttpRequest;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnit44Runner;

@RunWith(MockitoJUnit44Runner.class)
public class OAuthMessageSignerTest extends SignpostTestBase {

    @Test
    public void shouldCreateCorrectPlaintextSignature() throws Exception {
        OAuthMessageSigner signer = OAuthMessageSigner.create(SignatureMethod.PLAINTEXT);
        signer.setConsumerSecret(CONSUMER_SECRET);
        signer.setTokenSecret(TOKEN_SECRET);

        assertEquals(CONSUMER_SECRET + "&" + TOKEN_SECRET, signer.sign(httpGetMock,
                OAUTH_PARAMS));
    }

    @Test
    public void shouldComputeCorrectHmacSha1Signature() throws Exception {
        // based on the reference test case from http://oauth.pbwiki.com/TestCases
        OAuthMessageSigner signer = OAuthMessageSigner.create(SignatureMethod.HMAC_SHA1);
        signer.setConsumerSecret(CONSUMER_SECRET);
        signer.setTokenSecret(TOKEN_SECRET);

        HttpRequest request = mock(HttpRequest.class);
        when(request.getRequestUrl()).thenReturn(
                "http://photos.example.net/photos?file=vacation.jpg&size=original");
        when(request.getMethod()).thenReturn("GET");

        HashMap<String, String> oauthParams = new HashMap<String, String>(
                OAUTH_PARAMS);
        oauthParams.put("oauth_signature_method", "HMAC-SHA1");

        assertEquals("tR3+Ty81lMeYAr/Fid0kMTYa/WM=", signer.sign(request,
                oauthParams));
    }
}
