Signpost
=====================================

What is Signpost?
------------

Signpost is the easy and intuitive solution for signing HTTP messages on the Java platform in conformance with the [OAuth Core 1.0a](http://oauth.net/core/1.0a) standard. Signpost follows a modular and flexible design, allowing you to combine it with different HTTP messaging layers. Click here for a [list of supported HTTP libraries](docs/SupportedHttpLibraries.md).

Goals of Signpost
------------

Signpost has been designed with several principal goals in mind:

### Simplicity
Using Signpost is as simple as it could possibly get -- all actions are executed with only a few lines of code. For example, this is how you would sign a classic Java HTTP message using Signpost:

```java
        // create an HTTP request to a protected resource
        URL url = new URL("http://api.example.com/protected")
        HttpURLConnection request = (HttpURLConnection) url.openConnection();

        // sign the request (consumer is a Signpost DefaultOAuthConsumer)
        consumer.sign(request);

        // send the request
        request.connect();
```

Signpost exposes a minimalistic API designed for two purposes: Signing HTTP messages and requesting tokens from an OAuth service provider. Everything else is beyond the scope of the OAuth specification, and is thus left to the HTTP messaging layer, where it belongs.

For more exhaustive examples, please refer to [GettingStarted](docs/GettingStarted.md).

### Unobtrusiveness
Signpost tries to be as unobtrusive as possible. Unlike other implementations, Signpost does not wrap the entire HTTP layer and hides its features from the client. Instead, you simply pass an HttpRequest object to it, and Signpost will sign the message using the credentials it was configured with.

This means that all the power and flexibility of the underlying HTTP engine is still at your fingertips!

### Modularity
Since version 1.1, Signpost comes in modules. Apart from the core module, which you always need, you can download additional modules to support other HTTP messaging libraries than the one coming with the standard Java platform (which would be [java.net.HttpURLConnection](http://java.sun.com/javase/6/docs/api/java/net/HttpURLConnection.html)).

Apart from HttpURLConnection, Signpost currently has modules for [Apache Commons HTTP](http://hc.apache.org/) version 4, and [Jetty HTTP Client](http://docs.codehaus.org/display/JETTY/Jetty+HTTP+Client) version 6.

Limitations
------------
Signpost strives to be a simple library. In order to reduce API and implementation complexity, Signpost does currently not support the following things:

  * Message signing using public key encryption (as per [section 9.3](http://oauth.net/core/1.0#anchor19)) is currently unsupported. Message signing using the PLAINTEXT and HMAC-SHA1 methods is supported, however.
  * Writing OAuth protocol params to the [WWW-Authenticate](http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.47) header field

I believe that even with those restrictions in place, Signpost will work for the majority of its users. Trading in rarely used features for more simplicity and ease of use was a design decision. If that doesn't work for your setup, Signpost is probably not the best choice for you.

Thread Safety
------------
Signpost is not thread safe and probably will never be. Signpost objects are very lightweight, so you are adviced to create an OAuthConsumer and OAuthProvider for every thread in your application that must send signed HTTP requests. Both objects are also serializable, so you can persist and restore them later.

Google Android
------------
*IMPORTANT: Do NOT use the DefaultOAuth`*` implementations on Android, since there's a bug in Android's java.net.HttpURLConnection that keeps it from working with some service providers. Instead, use the CommonsHttpOAuth`*` classes, since they are meant to be used with Apache Commons HTTP (that's what Android uses for HTTP anyway).*

Signpost is already used in several applications running on Android, Google's software stack for mobile devices. In fact, Signpost has already signed thousands of HTTP requests at this very moment, as it is an integral part of [Qype Radar](http://www.qype.co.uk/go-mobile), our geo-sensitive mobile application for Android that finds the best places near you.

OAuth Service Providers
------------
If neither Signpost nor the OAuth service providers out there would be buggy, then Signpost would work with all of them. That's quite an optimistic expectation though, so on a slightly more conservative note, here's a list of service providers that have been tested to work with Signpost:

  * [Twitter](http://apiwiki.twitter.com) ([instructions](docs/TwitterAndSignpost.md), [code](https://github.com/mttkay/signpost-examples/tree/master/OAuthTwitterExample example))
  * [Google services](http://code.google.com/apis/accounts/docs/OAuth.html) ([example code](https://github.com/mttkay/signpost-examples/tree/master/OAuthGoogleExample))
  * [Netflix](https://github.com/mttkay/signpost-examples/tree/master/OAuthNetflixExample)


Support and discussions
------------
- API docs: http://mttkay.github.com/signpost/index.html
- Example code: http://github.com/mttkay/signpost-examples
- Please use the [Signpost Google Group](http://groups.google.com/group/signpost-users) for questions, feedback and discussion.

License
------------

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
