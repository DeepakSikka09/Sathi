package in.ecomexpress.sathi.utils;

import android.util.Base64;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;


public class EncryptionDecription {

    public static String encrypt(String value, String key)
            throws UnsupportedEncodingException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {

        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] key_data = digest.digest(key.getBytes());
        byte[] value_bytes = value.getBytes(StandardCharsets.UTF_8);
        byte[] iv = getKeyBytes(Base64.encodeToString(key_data, 0));
        return Base64.encodeToString(encrypt(value_bytes, key_data, iv), 0);
    }

    public static byte[] encrypt(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, byte[] paramArrayOfByte3)
            throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
        // setup AES cipher in CBC mode with PKCS #5 padding
        Cipher localCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");

        // encrypt
        localCipher.init(1, new SecretKeySpec(paramArrayOfByte2, "AES"), new IvParameterSpec(paramArrayOfByte3));
        return localCipher.doFinal(paramArrayOfByte1);
    }

    public static String decrypt(String value, String key)
            throws GeneralSecurityException, IOException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] key_data = digest.digest(key.getBytes());
        byte[] value_bytes = Base64.decode(value, 0);
        byte[] iv = getKeyBytes(Base64.encodeToString(key_data, 0));
        int len = value_bytes.length;
      //  SathiLogger.d("lenght", "" + len);
        return new String(decrypt(value_bytes, key_data, iv), StandardCharsets.UTF_8);
    }

    public static byte[] decrypt(byte[] ArrayOfByte1, byte[] ArrayOfByte2, byte[] ArrayOfByte3)
            throws InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
        // setup AES cipher in CBC mode with PKCS #5 padding
        Cipher localCipher = null;
        try {
            localCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        } catch (Exception p) {
           // SathiLogger.e(p.getMessage());
            p.printStackTrace();
        }

        // decrypt
        localCipher.init(2, new SecretKeySpec(ArrayOfByte2, "AES"), new IvParameterSpec(ArrayOfByte3));
        return localCipher.doFinal(ArrayOfByte1);
    }


    public static byte[] getKeyBytes(String paramString)
            throws UnsupportedEncodingException {
        byte[] arrayOfByte1 = new byte[16];
        byte[] arrayOfByte2 = paramString.getBytes(StandardCharsets.UTF_8);

        System.arraycopy(arrayOfByte2, 0, arrayOfByte1, 0, Math.min(arrayOfByte2.length, arrayOfByte1.length));
       // SathiLogger.d("byte1", Base64.encodeToString(arrayOfByte1, 0));
       // SathiLogger.d("Key", Base64.encodeToString(arrayOfByte2, 1));
        return arrayOfByte1;
    }
}