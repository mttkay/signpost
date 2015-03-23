
```java
        // create a consumer object and configure it with the access
        // token and token secret obtained from the service provider
        OAuthConsumer consumer = new CommonsHttpOAuthConsumer(CONSUMER_KEY,
                CONSUMER_SECRET);
        consumer.setTokenWithSecret(ACCESS_TOKEN, TOKEN_SECRET);

        // create an HTTP request to a protected resource
        HttpGet request = new HttpGet("http://example.com/protected");

        // sign the request
        consumer.sign(request);

        // send the request
        HttpClient httpClient = new DefaultHttpClient();
        HttpResponse response = httpClient.execute(request);
```
