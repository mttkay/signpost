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

    @Test
    public void shouldComputeCorrectRsaSha1Signature() throws Exception {
        // based on the reference test case from
        // http://oauth.pbwiki.com/TestCases
        final String PRIVATE_KEY =
                "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBALRiMLAh9iimur8V" +
                "A7qVvdqxevEuUkW4K+2KdMXmnQbG9Aa7k7eBjK1S+0LYmVjPKlJGNXHDGuy5Fw/d" +
                "7rjVJ0BLB+ubPK8iA/Tw3hLQgXMRRGRXXCn8ikfuQfjUS1uZSatdLB81mydBETlJ" +
                "hI6GH4twrbDJCR2Bwy/XWXgqgGRzAgMBAAECgYBYWVtleUzavkbrPjy0T5FMou8H" +
                "X9u2AC2ry8vD/l7cqedtwMPp9k7TubgNFo+NGvKsl2ynyprOZR1xjQ7WgrgVB+mm" +
                "uScOM/5HVceFuGRDhYTCObE+y1kxRloNYXnx3ei1zbeYLPCHdhxRYW7T0qcynNmw" +
                "rn05/KO2RLjgQNalsQJBANeA3Q4Nugqy4QBUCEC09SqylT2K9FrrItqL2QKc9v0Z" +
                "zO2uwllCbg0dwpVuYPYXYvikNHHg+aCWF+VXsb9rpPsCQQDWR9TT4ORdzoj+Nccn" +
                "qkMsDmzt0EfNaAOwHOmVJ2RVBspPcxt5iN4HI7HNeG6U5YsFBb+/GZbgfBT3kpNG" +
                "WPTpAkBI+gFhjfJvRw38n3g/+UeAkwMI2TJQS4n8+hid0uus3/zOjDySH3XHCUno" +
                "cn1xOJAyZODBo47E+67R4jV1/gzbAkEAklJaspRPXP877NssM5nAZMU0/O/NGCZ+" +
                "3jPgDUno6WbJn5cqm8MqWhW1xGkImgRk+fkDBquiq4gPiT898jusgQJAd5Zrr6Q8" +
                "AO/0isr/3aa6O6NLQxISLKcPDk2NOccAfS/xOtfOz4sJYM3+Bs4Io9+dZGSDCA54" +
                "Lw03eHTNQghS0A==";

        OAuthMessageSigner signer = new RsaSha1MessageSigner();
        signer.setConsumerSecret(PRIVATE_KEY);

        HttpRequest request = mock(HttpRequest.class);
        when(request.getRequestUrl()).thenReturn("http://photos.example.net/photos");
        when(request.getMethod()).thenReturn("GET");

        HttpParameters OAUTH_PARAMS = new HttpParameters();
        OAUTH_PARAMS.put("oauth_consumer_key", "dpf43f3p2l4k3l03");
        OAUTH_PARAMS.put("oauth_signature_method", "RSA-SHA1");
        OAUTH_PARAMS.put("oauth_timestamp", "1196666512");
        OAUTH_PARAMS.put("oauth_nonce", "13917289812797014437");
        OAUTH_PARAMS.put("oauth_version", "1.0");

        HttpParameters params = new HttpParameters();
        params.putAll(OAUTH_PARAMS);
        params.put("file", "vacaction.jpg");
        params.put("size", "original");

        assertEquals("jvTp/wX1TYtByB1m+Pbyo0lnCOLIsyGCH7wke8AUs3BpnwZJtAuEJkvQL2/9n4s5wUmUl4aCI4BwpraNx4RtEXMe5qg5T1LVTGliMRpKasKsW//e+RinhejgCuzoH26dyF8iY2ZZ/5D1ilgeijhV/vBka5twt399mXwaYdCwFYE=", signer.sign(request, params));
    }


}
