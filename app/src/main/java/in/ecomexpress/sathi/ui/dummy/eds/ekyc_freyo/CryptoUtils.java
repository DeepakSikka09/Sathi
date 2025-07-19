package in.ecomexpress.sathi.ui.dummy.eds.ekyc_freyo;

import android.util.Base64;

import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class CryptoUtils {
    public static final String RSA_TRANSFORMATION = "RSA/ECB/PKCS1Padding";

    public static byte[] generateKey() throws NoSuchAlgorithmException {
        return generateKey(128);
    }

    public static byte[] generateKey(int keySize) throws NoSuchAlgorithmException {
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(keySize);// Size of Key
        SecretKey aesKey = keyGenerator.generateKey();
        byte[] encoded = aesKey.getEncoded();
        return encoded;
    }

    public static byte[] generateIv() {
        SecureRandom secureRandom = new SecureRandom();
        byte[] initVectorBytes = new byte[16];
        secureRandom.nextBytes(initVectorBytes);
        return initVectorBytes;
    }

    public static byte[] encryptAES(byte[] keyBytes, byte[] dataBytes, byte[] ivBytes) throws GeneralSecurityException {

        IvParameterSpec ivParameterSpec = new IvParameterSpec(ivBytes);

        SecretKeySpec secretKeySpec = new SecretKeySpec(keyBytes, "AES");

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivParameterSpec);

        // Encrypt input text
        byte[] encrypted = cipher.doFinal(dataBytes);
        return encrypted;

    }

    public static byte[] decryptAes(byte[] keyBytes, byte[] dataBytes, byte[] ivBytes) throws GeneralSecurityException {
        IvParameterSpec ivParameterSpec = new IvParameterSpec(ivBytes);

        SecretKeySpec secretKeySpec = new SecretKeySpec(keyBytes, "AES");

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivParameterSpec);
        // Encrypt input text
        byte[] decrypted = cipher.doFinal(dataBytes);
        return decrypted;
    }

    public static String decrypt(String key, String data, String iv) {
        try {


            SecureRandom secureRandom = new SecureRandom();
            byte[] initVectorBytes = new byte[16];
            secureRandom.nextBytes(initVectorBytes);


            IvParameterSpec ivParameterSpec = new IvParameterSpec(Base64.decode(iv, Base64.DEFAULT));
            byte[] keyBytes = Base64.decode(key, Base64.DEFAULT);

            SecretKeySpec secretKeySpec = new SecretKeySpec(keyBytes, "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivParameterSpec);
            // Encrypt input text
            byte[] decrypted = cipher.doFinal(Base64.decode(data.getBytes("UTF-8"), Base64.DEFAULT));
            String result = new String(decrypted);
            return result;
        } catch (Exception e) {
           // FirebaseCrashlytics.getInstance().recordException(e);
            e.printStackTrace();
        }
        return "";
    }


    public static String encryptRSA(String strPublicKey, String data) {
        try {

                Cipher cipher = Cipher.getInstance(RSA_TRANSFORMATION);
            cipher.init(Cipher.ENCRYPT_MODE, getPemPublicKey(strPublicKey));

            // Encrypt input text
            byte[] encrypted = cipher.doFinal(data.getBytes("UTF-8"));
            return new String(Base64.encode(encrypted, Base64.NO_WRAP), "UTF-8");
        } catch (Exception e) {
         //   FirebaseCrashlytics.getInstance().recordException(e);
            e.printStackTrace();
        }
        return null;
    }

    public static String encryptRSA(String strPublicKey, byte[] dataBytes) throws Exception {

        Cipher cipher = Cipher.getInstance(RSA_TRANSFORMATION);
        cipher.init(Cipher.ENCRYPT_MODE, getPemPublicKey(strPublicKey));

        // Encrypt input text
        byte[] encrypted = cipher.doFinal(dataBytes);
        return new String(Base64.encode(encrypted, Base64.NO_WRAP), "UTF-8");

    }

    public static PublicKey getPemPublicKey(String temp) throws Exception {
        String publicKeyPEM = temp.replace("-----BEGIN PUBLIC KEY-----\n", "");
        publicKeyPEM = publicKeyPEM.replace("-----BEGIN PUBLIC KEY-----", "");
        publicKeyPEM = publicKeyPEM.replace("\n-----END PUBLIC KEY-----", "");
        publicKeyPEM = publicKeyPEM.replace("-----END PUBLIC KEY-----", "");
        System.out.println("CryptoUtils.getPemPublicKey "+publicKeyPEM);

        byte[] keyBytes = Base64.decode(publicKeyPEM, Base64.NO_WRAP);


        X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return kf.generatePublic(spec);
    }

    public static PrivateKey getPKCS8PrivateKey(String temp) throws Exception {

        String privateKeyStr = temp.replace("-----BEGIN PUBLIC KEY-----\n", "");
        privateKeyStr = privateKeyStr.replace("-----END PUBLIC KEY-----", "");

        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(Base64.decode(privateKeyStr, Base64.NO_WRAP));

        KeyFactory kf = KeyFactory.getInstance("RSA");
        return kf.generatePrivate(keySpec);
    }
}