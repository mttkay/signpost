package oauth.signpost.exception;

@SuppressWarnings("serial")
public class OAuthExpectationFailedException extends IllegalStateException {

    public OAuthExpectationFailedException(String message) {
        super(message);
    }
}
