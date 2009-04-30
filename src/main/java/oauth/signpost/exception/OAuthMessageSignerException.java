package oauth.signpost.exception;

@SuppressWarnings("serial")
public class OAuthMessageSignerException extends Exception {

    public OAuthMessageSignerException(String message) {
        super(message);
    }

    public OAuthMessageSignerException(Exception cause) {
        super(cause);
    }

}
