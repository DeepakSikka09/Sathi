package in.ecomexpress.sathi.ui.dummy.eds.eds_ekyc_hdfc;



import org.apache.commons.codec.binary.Base64;

import java.nio.charset.Charset;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

public class RSAEncrypterDecrypter {
	private static final String RSA_ECB_PKCS1PADDING = "RSA/ECB/PKCS1Padding";
	private static final String RSA = "RSA";

	/**
	 * Encrypt data using RSA/ECB/PKCS1Padding 
	 * @param data to be encrypted
	 * @param public key used for encryption
	 * @return encrypted result
	 */

//	public static String encrypt(String rawText, PublicKey publicKey) throws IOException, GeneralSecurityException {
//		Cipher cipher = Cipher.getInstance("RSA");
//		cipher.init(Cipher.ENCRYPT_MODE, publicKey);
//		return Base64.encodeBase64String(cipher.doFinal(rawText.getBytes("UTF-8")));
//	}

	public static byte[] rsaEncrypt(byte[] original, PublicKey key)
	{
		try
		{
			Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
			cipher.init(Cipher.ENCRYPT_MODE, key);
			return cipher.doFinal(original);
		}
		catch(Exception e)
		{
			  e.printStackTrace();
		}
		return null;
	}


	public byte[] rsadecrypt(byte[] data, byte[] key) {
		byte[] decryptedValue = null;
		try {
			SecretKeySpec secretKeySpec=new SecretKeySpec(key, RSA);
			Cipher cipher = Cipher.getInstance(RSA_ECB_PKCS1PADDING);
			cipher.init(Cipher.DECRYPT_MODE,secretKeySpec);
			decryptedValue = cipher.doFinal(data);
		}
		catch(NoSuchAlgorithmException exp) {
			//TODO handle exception
		}
		catch(NoSuchPaddingException exp) {
			//TODO handle exception
		}
		catch(IllegalBlockSizeException exp) {
			//TODO handle exception
		}
		catch(BadPaddingException exp) {
			//TODO handle exception
		}
		catch(InvalidKeyException exp) {
			//TODO handle exception
		}
		return decryptedValue;
	}



	public byte[] encrypt(byte[] data, byte[] key) {
		byte[] encryptedValue = null;
		try {
			SecretKeySpec secretKeySpec=new SecretKeySpec(key,RSA);
			Cipher cipher = Cipher.getInstance(RSA_ECB_PKCS1PADDING);
			cipher.init(Cipher.ENCRYPT_MODE,secretKeySpec);
			encryptedValue = cipher.doFinal(data);





		}
		catch(NoSuchAlgorithmException exp) {
			//TODO handle exception
		}
		catch(NoSuchPaddingException exp) {
			//TODO handle exception
		}
		catch(IllegalBlockSizeException exp) {
			//TODO handle exception
		}
		catch(BadPaddingException exp) {
			//TODO handle exception
		}
		catch(InvalidKeyException exp) {
			exp.printStackTrace();
			//TODO handle exception
		}
		return encryptedValue;
	}
	
	/**
	 * Decrypt data using RSA/ECB/PKCS1Padding 
	 * @param data to be decrypted
	 * @param private key used for decryption
	 * @return decrypted result
	 */
	public byte[] decrypt(byte[] data, byte[] key) {
		byte[] decryptedValue = null;
		try {
			SecretKeySpec secretKeySpec=new SecretKeySpec(key, RSA);
			Cipher cipher = Cipher.getInstance(RSA_ECB_PKCS1PADDING);
			cipher.init(Cipher.DECRYPT_MODE,secretKeySpec);
			decryptedValue = cipher.doFinal(data);
		}
		catch(NoSuchAlgorithmException exp) {
			//TODO handle exception
		}
		catch(NoSuchPaddingException exp) {
			//TODO handle exception
		}
		catch(IllegalBlockSizeException exp) {
			//TODO handle exception
		}
		catch(BadPaddingException exp) {
			//TODO handle exception
		}
		catch(InvalidKeyException exp) {
			//TODO handle exception
		}
		return decryptedValue;
	}

    public byte[] decryptkey(String data, PrivateKey key) {
       /* byte[] decryptedValue = null;
        try {
           *//* Cipher cipher = Cipher.getInstance(RSA_ECB_PKCS1PADDING);
            cipher.init(Cipher.DECRYPT_MODE, key);
            decryptedValue = cipher.doFinal(data);*//*
			Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
			cipher.init(Cipher.DECRYPT_MODE,key);
			decryptedValue = cipher.doFinal(data);

			String result = new String(decryptedValue, Charset.forName("UTF-8"));

*/

		byte[] result = new byte[0];
		try {
			Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1PADDING");
			cipher.init(Cipher.DECRYPT_MODE, key);
			byte[] decoded = Base64.decodeBase64(data.getBytes());
            String finala= new String(decoded);

			//String encryptedItem = org.apache.axis.encoding.Base64.encode(getSymmetricKeyEncryptedValue);


			//String dencryptedItem = org.apache.axis.decoding.Base64.decode(getSymmetricKeyEncryptedValue);
			result = cipher.doFinal(decoded);

			//String value=Base64.encod

		} catch (InvalidKeyException | NoSuchAlgorithmException
				| NoSuchPaddingException | IllegalBlockSizeException
				| BadPaddingException e) {
			try {
				throw new GeneralSecurityException(
						"unable to decrypt old encryption");
			} catch (GeneralSecurityException ex) {
				ex.printStackTrace();
			}
		}


		return result;
	}
       /* catch(NoSuchAlgorithmException exp) {
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
        return decryptedValue;*/
    //}
	
}
