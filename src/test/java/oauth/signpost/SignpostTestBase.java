package oauth.signpost;

import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.when;

import java.util.HashMap;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.DefaultHttpResponseFactory;
import org.apache.http.protocol.BasicHttpContext;
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

    public static final HashMap<String, String> OAUTH_PARAMS = new HashMap<String, String>();

    @Mock
    OAuthConsumer consumerMock;

    @Mock
    HttpClient httpClientMock;

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
    public void initMocks() throws Exception {
        MockitoAnnotations.initMocks(this);

        // init consumer mock
        when(consumerMock.getConsumerKey()).thenReturn(CONSUMER_KEY);
        when(consumerMock.getConsumerSecret()).thenReturn(CONSUMER_SECRET);
        when(consumerMock.getToken()).thenReturn(TOKEN);
        when(consumerMock.getTokenSecret()).thenReturn(TOKEN_SECRET);

        // init httpclient mock
        HttpResponse response = new DefaultHttpResponseFactory().newHttpResponse(
                HttpVersion.HTTP_1_1, HttpStatus.SC_OK, new BasicHttpContext());
        StringEntity entity = new StringEntity(OAuth.OAUTH_TOKEN + "=" + TOKEN
                + "&" + OAuth.OAUTH_TOKEN_SECRET + "=" + TOKEN_SECRET, "UTF-8");
        response.setEntity(entity);

        when(httpClientMock.execute((HttpGet) anyObject())).thenReturn(response);
    }
}
