package oauth.signpost.commonshttp;

/**
 * An extension of the {@link CommonsHttpOAuthConsumer} that allows timestamp and nonce to be specified.
 * Useful for validating the signature of incoming requests. In order to perform the validation, 
 * just generate a signature for the same method, URL, consumer key, timestamp and nonce as the incoming request, and
 * use the shared secret that you know. If the generated signature matches the one of the incoming request, then
 * it is valid.
 */
public class ValidatingCommonsOAuthConsumer extends CommonsHttpOAuthConsumer {

	private final String timestamp;
	private final String nonce;

	public ValidatingCommonsOAuthConsumer(String consumerKey, String consumerSecret, String timestamp, String nonce) {
		super(consumerKey, consumerSecret);
		this.timestamp = timestamp;
		this.nonce = nonce;
	}

	@Override
	protected String generateTimestamp() {
		return timestamp;
	}

	@Override
	protected String generateNonce() {
		return nonce;
	}
}
