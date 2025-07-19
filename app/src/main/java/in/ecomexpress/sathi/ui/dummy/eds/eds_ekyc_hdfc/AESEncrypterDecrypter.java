package in.ecomexpress.sathi.ui.dummy.eds.eds_ekyc_hdfc;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Encrypt/Decrypt data using symmetric encryption and key
 * @author Madhura Oak
 * 
 */
public class AESEncrypterDecrypter {
	private static final byte[] IV = "1234567890123456".getBytes();
	private static final String AES_CBC_PKCS5PADDING = "AES/CBC/PKCS5Padding";
	private static final String AES = "AES";
	
	/**
	 * Encrypt data using AES/CBC/PKCS5Padding. IV appears first in the returned data array followed with encrypted request. 
	 * @param data to be encrypted
	 * @param key used for encryption
	 * @return encrypted result
	 */


//	public static String encrypt(String strToEncrypt, String secret)
//	{
//		try
//		{
//			byte[] iv = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
//			IvParameterSpec ivspec = new IvParameterSpec(iv);
//
//			SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
//			KeySpec spec = new PBEKeySpec(secretKey.toCharArray(), salt.getBytes(), 65536, 256);
//			SecretKey tmp = factory.generateSecret(spec);
//			SecretKeySpec secretKey = new SecretKeySpec(tmp.getEncoded(), "AES");
//
//			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
//			cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivspec);
//			return Base64.getEncoder().encodeToString(cipher.doFinal(strToEncrypt.getBytes("UTF-8")));
//		}
//		catch (Exception e)
//		{
//			System.out.println("Error while encrypting: " + e.toString());
//		}
//		return null;
//	}
	public  String encrypt(String data, byte[] key) {
	//	byte[]
				String encryptedValue = null;
		try {
			SecretKeySpec secretKeySpec=new SecretKeySpec(key,AES);
			Cipher cipher = Cipher.getInstance(AES_CBC_PKCS5PADDING);
			cipher.init(Cipher.ENCRYPT_MODE,secretKeySpec, new IvParameterSpec(IV));
			byte[] dataArr = data.getBytes();
			byte[] ivAndData = new byte[IV.length + dataArr.length];
			System.arraycopy(IV, 0, ivAndData, 0, IV.length);
			System.arraycopy(dataArr, 0, ivAndData, IV.length,dataArr.length);
			//encryptedValue =
				encryptedValue=	Base64.getEncoder().encodeToString(cipher.doFinal(ivAndData));
		}
		catch(NoSuchAlgorithmException exp) {
			//TODO handle exception
			exp.printStackTrace();
		}
		catch(NoSuchPaddingException exp) {
			//TODO handle exception
			exp.printStackTrace();
		}
		catch(IllegalBlockSizeException exp) {
			//TODO handle exception
			exp.printStackTrace();
		}
		catch(BadPaddingException exp) {
			//TODO handle exception
			exp.printStackTrace();
		}
		catch(InvalidKeyException exp) {
			//TODO handle exception
			exp.printStackTrace();
		}
		catch(InvalidAlgorithmParameterException exp) {
			//TODO handle exception
			exp.printStackTrace();
		}
		return 	encryptedValue;
	}
	
	/**
	 * Decrypt encrypted data using AES/CBC/PKCS5Padding and a secret key. IV should appear first in the byte array argument followed with encrypted value of response 
	 * @param data to be decrypted
	 * @param key
	 * @return decrypted value
	 */
	public String decrypt(String encrypted, byte[] key) {
		String decryptedData = null;
		try {
			SecretKeySpec secretKeySpec=new SecretKeySpec(key,AES);
			Cipher cipher = Cipher.getInstance(AES_CBC_PKCS5PADDING);
			cipher.init(Cipher.DECRYPT_MODE,secretKeySpec, new IvParameterSpec(IV));
			byte[] decryptedDataWithIV = cipher.doFinal(encrypted.getBytes());

			decryptedData = String.valueOf(new byte[decryptedDataWithIV.length-IV.length]);
			//System.arraycopy(decryptedDataWithIV, IV.length, decryptedData, 0, decryptedData.length);
			System.out.println(decryptedData);
		}
		catch(NoSuchAlgorithmException exp) {
			//TODO handle exception
			exp.printStackTrace();
		}
		catch(NoSuchPaddingException exp) {
			//TODO handle exception
			exp.printStackTrace();
		}
		catch(IllegalBlockSizeException exp) {
			//TODO handle exception
			exp.printStackTrace();
		}
		catch(BadPaddingException exp) {
			//TODO handle exception
			exp.printStackTrace();
		}
		catch(InvalidKeyException exp) {
			//TODO handle exception
			exp.printStackTrace();
		}
		catch(InvalidAlgorithmParameterException exp) {
			//TODO handle exception
			exp.printStackTrace();
		}
//		catch(GeneralSecurityException exp) {
//			//TODO handle exception
//			exp.printStackTrace();
//		}
		return decryptedData;
	}
	public static String decrypt1( String encrypted,byte[] key) throws GeneralSecurityException {
		// Argument validation.
		if (key.length != 32) {
			throw new IllegalArgumentException("Invalid key size.");
		}

		// Setup AES tool.
		SecretKeySpec skeySpec = new SecretKeySpec(key, "AES");
		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		cipher.init(Cipher.DECRYPT_MODE, skeySpec, new IvParameterSpec(new byte[16]));

		// Do the job with AES tool.
		byte[] binary = org.apache.axis.encoding.Base64.decode(encrypted);
		byte[] original = cipher.doFinal(binary);
		return new String(original, StandardCharsets.UTF_8);
	}
}
