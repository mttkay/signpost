
```java
        // create a consumer object and configure it with the access
        // token and token secret obtained from the service provider
        OAuthConsumer consumer = new JettyOAuthConsumer(CONSUMER_KEY,
                CONSUMER_SECRET);
        consumer.setTokenWithSecret(ACCESS_TOKEN, TOKEN_SECRET);

        // create an HTTP request to a protected resource
        ContentExchange request = new ContentExchange();
        request.setMethod("GET");
        request.setURL("http://example.com/protected");

        // sign the request
        consumer.sign(request);

        // send the request
        HttpClient client = new HttpClient();
        client.start();
        client.send(request);

        request.waitForDone();
```
