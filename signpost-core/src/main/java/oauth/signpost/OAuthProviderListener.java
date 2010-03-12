package oauth.signpost;

import oauth.signpost.http.HttpRequest;
import oauth.signpost.http.HttpResponse;

/**
 * Provides hooks into the token request handling procedure executed by
 * {@link OAuthProvider}.
 * 
 * @author Matthias Kaeppler
 */
public interface OAuthProviderListener {

    /**
     * Called after the request has been created and default headers added, but
     * before the request has been signed.
     * 
     * @param request
     *        the request to be sent
     * @throws Exception
     */
    void prepareRequest(HttpRequest request) throws Exception;

    /**
     * Called after the request has been signed, but before it's being sent.
     * 
     * @param request
     *        the request to be sent
     * @throws Exception
     */
    void prepareSubmission(HttpRequest request) throws Exception;

    /**
     * Called when the server response has been received. You can implement this
     * to manually handle the response data.
     * 
     * @param request
     *        the request that was sent
     * @param response
     *        the response that was received
     * @return returning true means you have handled the response, and the
     *         provider will return immediately. Return false to let the event
     *         propagate and let the provider execute its default response
     *         handling.
     * @throws Exception
     */
    boolean onResponseReceived(HttpRequest request, HttpResponse response) throws Exception;
}
