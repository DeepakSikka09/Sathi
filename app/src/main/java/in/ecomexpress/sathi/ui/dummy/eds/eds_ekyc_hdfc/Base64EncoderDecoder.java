package in.ecomexpress.sathi.ui.dummy.eds.eds_ekyc_hdfc;

import java.util.Base64;

/**
 * Base 64 encoding
 * @author Madhura Oak
 *
 */
public class Base64EncoderDecoder {
	/**
	 * Base 64 encoding of string
	 * @param String to be encoded
	 * @return encoded byte array
	 */
	public byte[] encode(String value) {
		return Base64.getEncoder().encode(value.getBytes());
	}
	
	/**
	 * Base 64 encoding of string
	 * @param String to be encoded
	 * @return encoded string
	 */
	public String encodeToString(String value) {
		return new String(encode(value));
	}

	
	/**
	 * Base 64 encoding of byte array
	 * @param byte array to be encoded
	 * @return encoded byte array
	 */
	public byte[] encode(byte[] value) {
		return Base64.getEncoder().encode(value);
	}

	/**
	 * Base 64 decoding of byte array
	 * @param value to be decoded
	 * @return decoded value
	 */
	public byte[] decode(byte[] value) {
		return Base64.getDecoder().decode(value);
	}
}
