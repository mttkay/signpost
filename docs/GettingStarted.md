# Getting Signpost

Get the latest Signpost build from the [download page](http://code.google.com/p/oauth-signpost/downloads/list)

OR

[checkout the source code](http://github.com/kaeppler/signpost) using Git:

`git clone git://github.com/kaeppler/signpost.git`

Now `cd signpost/` and run:

`mvn install`

This will download all dependencies and create a JAR in the target/ folder. Note that this step requires that you have the [Apache Maven](http://maven.apache.org) build system and [Git](http://www.git-scm.com) installed on your system.

OR

If you use [Apache Maven](http://maven.apache.org) for project management yourself, you can simply declare Signpost as a dependency in your pom.xml:

```
    <dependencies>
      <dependency>
        <groupId>oauth.signpost</groupId>
        <artifactId>signpost-core</artifactId>
        <version>1.2</version>
        <scope>compile</scope>
      </dependency>
    </dependencies>
```

Depending on your requirements, you may need to add dependencies to other Signpost modules (e.g. signpost-jetty6).

If you need to depend on an unreleased version, you have to add the Signpost snapshots repository to your POM:

```
    <repositories>
        <repository>
            <id>signpost-snapshots</id>
            <url>http://oss.sonatype.org/content/repositories/signpost-snapshots</url>
            <releases>
              <enabled>false</enabled>
            </releases>
            <snapshots>
              <enabled>true</enabled>
            </snapshots>
        </repository>
    </repositories>
```

The snapshots are kindly hosted by [Sonatype](http://oss.sonatype.org).

# Setting up Signpost

If you downloaded the JARs manually, you must also have the following libraries in your project's build path if you want to use Signpost:

  * [Apache Commons Codec 1.3](http://commons.apache.org/codec/) (or newer)

If you built Signpost using Maven in the previous step, then you do not need to manually install dependencies, since Maven will do that for you.

By default, Signpost supports signing HTTP requests of type java.net.HttpURLConnection. If you only need that, then you're good to go and you can skip to the next section. If you want to use a different HTTP messaging system, you must download an adapter module that supports adapting request objects of that library for Signpost being able to sign them. The adapter module must be added to your project's build path.

For a list of available adapter modules, refer to [SupportedHttpLibraries](SupportedHttpLibraries.md).

# Using Signpost

_All examples below assume that you have already obtained a consumer key and secret from the OAuth service provider you are communicating with._

**Android users: Do NOT use the DefaultOAuth`*` implementations on Android, since there's a bug in Android's java.net.HttpURLConnection that keeps it from working with some service providers. Instead, use the CommonsHttpOAuth`*` classes, since they are meant to be used with Apache Commons HTTP (that's what Android uses for HTTP anyway).**

## Signing an HTTP message using OAuthConsumer
**This section shows how to sign HTTP requests of type java.net.HttpURLConnection, which is the default. If you need to sign requests for other HTTP request types, please have a look at the examples in [SupportedHttpLibraries](SupportedHttpLibraries.md).**

If you have already obtained an access token from your service provider that allows you to access a protected resource, you can sign a request to that resource using Signpost as follows:

```
        // create a consumer object and configure it with the access
        // token and token secret obtained from the service provider
        OAuthConsumer consumer = new DefaultOAuthConsumer(CONSUMER_KEY,
                                             CONSUMER_SECRET);
        consumer.setTokenWithSecret(ACCESS_TOKEN, TOKEN_SECRET);

        // create an HTTP request to a protected resource
        URL url = new URL("http://example.com/protected");
        HttpURLConnection request = (HttpURLConnection) url.openConnection();

        // sign the request
        consumer.sign(request);

        // send the request
        request.connect();
```

**NOTE:** When using HttpURLConnection, you cannot sign POST requests that carry query parameters in the message payload (i.e. requests of type application/x-www-form-urlencoded). This is not a limitation of Signpost per se, but with the way URLConnection works. Server communication with URLConnection is based on data streams, which means that whenever you write something to the connection, it will be sent to the server immediately. This data is not buffered, and there is simply no way for Signpost to inspect that data and include it in a signature. Hence, when you have to sign requests which contain parameters in their body, you have to use an HTTP library like Apache Commons HttpComponents and the respective Signpost module. (This restriction does not apply to requests which send binary data such as documents or files, because that data won't become part of the signature anyway.)

## Obtaining a request token using OAuthProvider
Obtaining a request token from the OAuth service provider is the first step in the 3-way handshake defined by OAuth. In a second step (which is beyond the scope of Signpost or any OAuth library) the user must then authorize this request token by granting your application access to protected resources on a special website defined by the OAuth service provider.
```
        // create a new service provider object and configure it with
        // the URLs which provide request tokens, access tokens, and
        // the URL to which users are sent in order to grant permission
        // to your application to access protected resources
        OAuthProvider provider = new DefaultOAuthProvider(
                REQUEST_TOKEN_ENDPOINT_URL, ACCESS_TOKEN_ENDPOINT_URL,
                AUTHORIZE_WEBSITE_URL);

        // fetches a request token from the service provider and builds
        // a url based on AUTHORIZE_WEBSITE_URL and CALLBACK_URL to
        // which your app must now send the user
        String url = provider.retrieveRequestToken(consumer, CALLBACK_URL);
```
If your application cannot receive callbacks (e.g. because it's a desktop app), then you must replace CALLBACK\_URL with one of these values:
  * If the service provider you're communicating with implements version 1.0a of the protocol, then you must pass "oob" or `OAuth.OUT_OF_BAND` to indicate that you cannot receive callbacks.
  * If the service provider is still using the older 1.0 protocol, then you must pass `null` to indicate that you cannot receive callbacks.

**If you get a 401 during these steps:** Please make sure that when passing a callback URL, your applications is registered as being able to receive callbacks from your service provider. If you do NOT do that, then the service provider may decide to reject your request, because it thinks it's illegitimate. Twitter, for instance, will do this.

## Obtaining an access token using OAuthProvider
The third and last step in the "OAuth dance" is to exchange the blessed request token for an access token, which the client can then use to access protected resources on behalf of the user. Again, this is very simple to do with Signpost:
```
        provider.retrieveAccessToken(consumer, verificationCode);
```
The `verificationCode` is only meaningful for service providers implementing OAuth 1.0a. Depending on whether you provided a callback URL or out-of-band before, this value is either being passed to your application during callback as the `oauth_verifier` request parameter, or you must obtain this value manually from the user of your application.

On success, the OAuthConsumer connected to this OAuthProvider has now a valid access token and token secret set, and can start signing messages!
