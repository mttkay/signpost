package oauth.signpost;

import static org.mockito.Mockito.when;
import oauth.signpost.http.HttpRequest;
import oauth.signpost.http.HttpParameters;

import org.junit.Before;
import org.junit.BeforeClass;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public abstract class SignpostTestBase {

    public static final String OAUTH_VERSION = "1.0";

    public static final String CONSUMER_KEY = "dpf43f3p2l4k3l03";

    public static final String CONSUMER_SECRET = "kd94hf93k423kf44";

    public static final String TOKEN = "nnch734d00sl2jdk";

    public static final String TOKEN_SECRET = "pfkkdhi9sl3r4s00";

    public static final String NONCE = "kllo9940pd9333jh";

    public static final String TIMESTAMP = "1191242096";

    public static final String SIGNATURE_METHOD = "HMAC-SHA1";

    public static final String REQUEST_TOKEN_ENDPOINT_URL = "http://api.test.com/request_token";

    public static final String ACCESS_TOKEN_ENDPOINT_URL = "http://api.test.com/access_token";

    public static final String AUTHORIZE_WEBSITE_URL = "http://www.test.com/authorize";

    public static final HttpParameters OAUTH_PARAMS = new HttpParameters();

    public static final HttpParameters EMPTY_PARAMS = new HttpParameters();

    @Mock
    protected HttpRequest httpGetMock;

    @Mock
    protected HttpRequest httpPostMock;

    @BeforeClass
    public static void initOAuthParams() {
        OAUTH_PARAMS.put("oauth_consumer_key", CONSUMER_KEY);
        OAUTH_PARAMS.put("oauth_signature_method", SIGNATURE_METHOD);
        OAUTH_PARAMS.put("oauth_timestamp", TIMESTAMP);
        OAUTH_PARAMS.put("oauth_nonce", NONCE);
        OAUTH_PARAMS.put("oauth_version", OAUTH_VERSION);
        OAUTH_PARAMS.put("oauth_token", TOKEN);
    }

    @Before
    public void initRequestMocks() {
        MockitoAnnotations.initMocks(this);

        when(httpGetMock.getMethod()).thenReturn("GET");
        when(httpGetMock.getRequestUrl()).thenReturn("http://www.example.com");

        when(httpPostMock.getMethod()).thenReturn("POST");
        when(httpPostMock.getRequestUrl()).thenReturn("http://www.example.com");
    }

}
