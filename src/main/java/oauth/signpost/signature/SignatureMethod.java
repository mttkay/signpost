package oauth.signpost.signature;

public enum SignatureMethod {

    PLAINTEXT, HMAC_SHA1;

    @Override
    public String toString() {
        return super.toString().replace('_', '-');
    }
}
