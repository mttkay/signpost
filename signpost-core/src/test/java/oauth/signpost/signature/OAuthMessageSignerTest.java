package oauth.signpost.signature;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import oauth.signpost.SignpostTestBase;
import oauth.signpost.http.HttpRequest;
import oauth.signpost.http.HttpParameters;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnit44Runner;

@RunWith(MockitoJUnit44Runner.class)
public class OAuthMessageSignerTest extends SignpostTestBase {

    @Test
    public void shouldCreateCorrectPlaintextSignature() throws Exception {
        OAuthMessageSigner signer = new PlainTextMessageSigner();
        signer.setConsumerSecret(CONSUMER_SECRET);
        signer.setTokenSecret(TOKEN_SECRET);

        assertEquals(CONSUMER_SECRET + "&" + TOKEN_SECRET, signer.sign(httpGetMock,
                OAUTH_PARAMS));
    }

    @Test
    public void shouldComputeCorrectHmacSha1Signature() throws Exception {
        // based on the reference test case from
        // http://oauth.pbwiki.com/TestCases
        OAuthMessageSigner signer = new HmacSha1MessageSigner();
        signer.setConsumerSecret(CONSUMER_SECRET);
        signer.setTokenSecret(TOKEN_SECRET);

        HttpRequest request = mock(HttpRequest.class);
        when(request.getRequestUrl()).thenReturn("http://photos.example.net/photos");
        when(request.getMethod()).thenReturn("GET");

        HttpParameters params = new HttpParameters();
        params.putAll(OAUTH_PARAMS);
        params.put("file", "vacation.jpg");
        params.put("size", "original");

        assertEquals("tR3+Ty81lMeYAr/Fid0kMTYa/WM=", signer.sign(request, params));
    }

    @Test
    public void shouldComputeCorrectHmacSha256Signature() throws Exception {
        // based on the reference test case from
        // http://oauth.pbwiki.com/TestCases
        OAuthMessageSigner signer = new HmacSha256MessageSigner();
        signer.setConsumerSecret(CONSUMER_SECRET);
        signer.setTokenSecret(TOKEN_SECRET);

        HttpRequest request = mock(HttpRequest.class);
        when(request.getRequestUrl()).thenReturn("http://photos.example.net/photos");
        when(request.getMethod()).thenReturn("GET");

        HttpParameters params = new HttpParameters();
        params.putAll(OAUTH_PARAMS);
        params.put("file", "vacation.jpg");
        params.put("size", "original");

        assertEquals("0gCtTYQAxqCKhIE0sltgx7UgHkAs10vrpuYE7xpRBnE=", signer.sign(request, params));
    }
}
