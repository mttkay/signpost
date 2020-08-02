package oauth.signpost.signature;

import java.io.IOException;

/**
 * Minimal Base64 implementation.
 *
 * Base64 support in Android: android.util.Base64 (since API Level 8), java.util.Base64 (since API Level 26)
 *
 * Extracted from https://github.com/google/guava/blob/master/guava/src/com/google/common/io/BaseEncoding.java
 *
 * @see java.util.Base64
 * @apiNote https://www.ietf.org/rfc/rfc2045.txt
 */
class Base64 {
    private static final String BASE64 = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";
    private static final char BASE64_PADDING = '=';
    private static final int BASE64_BITS_PER_CHAR = 6;
    private static final int BASE64_BYTES_PER_CHUNK = 3;

    static String encode(byte[] bytes) {
        final StringBuilder stringBuilder = new StringBuilder();
        try {
            encodeTo(stringBuilder, bytes, 0, bytes.length);
        } catch (IOException ex) {
            throw new IllegalStateException(ex);
        }
        return stringBuilder.toString();
    }

    static void encodeTo(Appendable target, byte[] bytes, int off, int len) throws IOException {
        for (int i = 0; i < len; i += BASE64_BYTES_PER_CHUNK) {
            encodeChunkTo(target, bytes, off + i, Math.min(BASE64_BYTES_PER_CHUNK, len - i));
        }
    }

    static void encodeChunkTo(Appendable target, byte[] bytes, int off, int len) throws IOException {
        long bitBuffer = 0;
        for (int i = 0; i < len; ++i) {
            bitBuffer |= bytes[off + i] & 0xFF;
            bitBuffer <<= 8; // Add additional zero byte in the end.
        }
        // Position of first character is length of bitBuffer minus bitsPerChar.
        final int bitOffset = (len + 1) * 8 - BASE64_BITS_PER_CHAR;
        int bitsProcessed = 0;
        while (bitsProcessed < len * 8) {
            int charIndex = (int) (bitBuffer >>> (bitOffset - bitsProcessed)) & (BASE64.length() - 1);
            target.append(BASE64.charAt(charIndex));
            bitsProcessed += BASE64_BITS_PER_CHAR;
        }
        while (bitsProcessed < BASE64_BYTES_PER_CHUNK * 8) {
            target.append(BASE64_PADDING);
            bitsProcessed += BASE64_BITS_PER_CHAR;
        }
    }
}
