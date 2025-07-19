package in.ecomexpress.sathi.utils;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import com.google.common.base.Joiner;

import org.apache.commons.lang3.StringUtils;


public class HMACHasherAndMatcher {

    private static final Joiner JOINER = Joiner.on("");
    private static final int CLEAR_SIGN_BIT_MASK = 0x7fffffff;
    private static final int OFFSET_MASKING = 0x0000000f;
    private static final String PAD_STRING = "0";
    private static final int OUTPUT_LENGTH = 6;
    private static final String KEY_ALGO = "RAW";
    private static final String MAC_ALGO = "HmacSHA512";


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public boolean matchHashedVerificationCode(final String verificationCode, final String hashedVerificationCode, final List<String> hashingSalts)
            throws IllegalArgumentException, InvalidKeyException, NoSuchAlgorithmException {
        String computedHashedVerificationCode = computeHashedVerificationCode(verificationCode, hashingSalts);
        return StringUtils.equals(hashedVerificationCode, computedHashedVerificationCode);
    }



    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public String computeHashedVerificationCode(final String verificationCode, final List<String> hashingSalts)
            throws NoSuchAlgorithmException, InvalidKeyException {

        Mac mac;

        // the hash of this buffer is calculated
        final ByteBuffer buffer = ByteBuffer.allocate(8);
        buffer.putLong(0, 1);

        // build key
        String keyParam = JOINER.join(hashingSalts, verificationCode);

        // feed key into a mac instance

        final Key key = new SecretKeySpec(keyParam.getBytes(StandardCharsets.US_ASCII), KEY_ALGO);
        mac = Mac.getInstance(MAC_ALGO);
        mac.init(key);

        // compute hmac of buffer using key specified in mac
        final byte[] hmac = mac.doFinal(buffer.array());


        final int offset = hmac[hmac.length - 1] & OFFSET_MASKING;

        // selects 4 bytes from the result of the HMAC
        for (int i = 0; i < 4; i++) {
            buffer.put(i, hmac[i + offset]);
        }

        // The mask sets the result's most significant bit (sign-bit) to zero
        final int hotp = buffer.getInt(0) & CLEAR_SIGN_BIT_MASK;

        // take modulus with desired number of digits
        long verificationCodeValue = hotp % calculatePowerOf10(Integer.valueOf(OUTPUT_LENGTH));

        // Pad result for desired length with zeroes if required
        String hashedVerificationCode = StringUtils.rightPad(String.valueOf(verificationCodeValue), OUTPUT_LENGTH,
                PAD_STRING);

        return hashedVerificationCode;
    }

    /**
     * We are implementing own power function instead of Math.pow() as it is
     * buggy https://github.com/google/google-authenticator/issues/395
     *
     * @param power radix to calculate
     * @return power of 10
     */
    private long calculatePowerOf10(int power) {
        long result = 10;
        for (int i = 1; i < power; i++) {
            result = result * 10;
        }
        return result;
    }
}
