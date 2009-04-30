/**
 * 
 */
package oauth.signpost;

import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;
import oauth.signpost.exception.OAuthNotAuthorizedException;
import oauth.signpost.impl.DefaultOAuthConsumer;

import org.apache.http.client.HttpClient;

/**
 * @author matthias
 */
public interface OAuthProvider {

    /**
     * Use this method to set a custom {@link HttpClient} to use when retrieving
     * tokens from an HTTP server.
     */
    public void setHttpClient(HttpClient httpClient);

    /**
     * Queries the service provider for a request token.
     * <p>
     * <b>Pre-conditions:</b> the {@link DefaultOAuthConsumer} connected to
     * this provider must have a valid consumer key and consumer secret already
     * set.
     * </p>
     * <p>
     * <b>Post-conditions:</b> the {@link DefaultOAuthConsumer} connected to
     * this provider will have an unauthorized request token and token secret
     * set.
     * </p>
     * 
     * @param callbackUrl
     *            Used to construct the return value, see below
     * @return The URL to which the user must be sent in order to authorize the
     *         consumer. It include the unauthorized request token and the
     *         callback URL.
     * @throws OAuthMessageSignerException
     *             if signing the request failed
     * @throws OAuthNotAuthorizedException
     *             if the service provider rejected the consumer
     * @throws OAuthExpectationFailedException
     *             if required parameters were not correctly set by the consumer
     *             or service provider
     * @throws OAuthCommunicationException
     *             if server communication failed
     */
    public String retrieveRequestToken(String callbackUrl)
            throws OAuthMessageSignerException, OAuthNotAuthorizedException,
            OAuthExpectationFailedException, OAuthCommunicationException;

    /**
     * Queries the service provider for an access token.
     * <p>
     * <b>Pre-conditions:</b> the {@link DefaultOAuthConsumer} connected to
     * this provider must have a valid consumer key, consumer secret, authorized
     * request token and token secret already set.
     * </p>
     * <p>
     * <b>Post-conditions:</b> the {@link DefaultOAuthConsumer} connected to
     * this provider will have an access token and token secret set.
     * </p>
     * 
     * @throws OAuthMessageSignerException
     *             if signing the request failed
     * @throws OAuthNotAuthorizedException
     *             if the service provider rejected the consumer
     * @throws OAuthExpectationFailedException
     *             if required parameters were not correctly set by the consumer
     *             or service provider
     * @throws OAuthCommunicationException
     *             if server communication failed
     */
    public void retrieveAccessToken() throws OAuthMessageSignerException,
            OAuthNotAuthorizedException, OAuthExpectationFailedException,
            OAuthCommunicationException;
}
