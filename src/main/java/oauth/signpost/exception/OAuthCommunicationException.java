package oauth.signpost.exception;

@SuppressWarnings("serial")
public class OAuthCommunicationException extends Exception {

    public OAuthCommunicationException(Exception cause) {
        super("Communication with the service provider failed: "
                + cause.getLocalizedMessage(), cause);
    }
}
