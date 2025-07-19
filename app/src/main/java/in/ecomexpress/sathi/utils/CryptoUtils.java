package in.ecomexpress.sathi.utils;

import android.graphics.Bitmap;
import android.util.Base64;
import android.util.Log;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class CryptoUtils {

    private static SecretKeySpec secretKey;
    @SuppressWarnings("FieldCanBeLocal")
    private static byte[] key;

    public static boolean encryptFile(String inFilePath, String outFilePath, String key) {
        try {
            setKey(key);
            File f = new File(inFilePath);
            InputStream is = new FileInputStream(f);
            byte[] content = new byte[is.available()];
            is.read(content);
            byte[] encryptedData = encrypt(secretKey, content);
            FileOutputStream fos = new FileOutputStream(outFilePath);
            fos.write(encryptedData);
            fos.close();
            return true;
        } catch (Exception e) {
           // SathiLogger.e(e.getMessage());
            //System.out.println("Error while encrypting File: " + e.toString());
            return false;
        }
    }

    public static  byte[] decryptFile(String inFilePath, String outFilePath ,String key) {
        byte[] decryptedData = null;
        try {
            setKey(key);
            File f = new File(inFilePath);
            InputStream is = new FileInputStream(f);
            byte[] content = new byte[is.available()];
            is.read(content);
             decryptedData = decrypt(secretKey, content);
            FileOutputStream fos = new FileOutputStream(outFilePath);
            fos.write(decryptedData);
            fos.close();
           // return true;
        } catch (Exception e) {
           // SathiLogger.e(e.getMessage());//  System.out.println("Error while decrypting File: " + e.toString());
        }
        return decryptedData;
        //return false;
    }
    public static byte[] decryptFile1(String inFilePath ,String key) {
        try {
            setKey(key);
            File f = new File(inFilePath);
            InputStream is = new FileInputStream(f);
            byte[] content = new byte[is.available()];
            is.read(content);
            byte[] decryptedData = decrypt(secretKey, content);
//            FileOutputStream fos = new FileOutputStream(outFilePath);
//            fos.write(decryptedData);
//            fos.close();
            return decryptedData;
        } catch (Exception e) {
           // SathiLogger.e(e.getMessage());//  System.out.println("Error while decrypting File: " + e.toString());
        }

        return null;
    }
    public static String encryptString(String strToEncrypt, String secret) {
        try {
            setKey(secret);
            return Base64.encodeToString(encrypt(secretKey, strToEncrypt.getBytes(StandardCharsets.UTF_8)),Base64.DEFAULT);
        } catch (Exception e) {
           // SathiLogger.e(e.getMessage());
          //  System.out.println("Error while encrypting: " + e.toString());
        }
        return null;
    }

    public static String decryptString(String strToDecrypt, String secret) {
        try {
            setKey(secret);
            return new String(decrypt(secretKey, Base64.decode(strToDecrypt,Base64.DEFAULT)));
        } catch (Exception e) {
           // SathiLogger.e(e.getMessage());
         //   System.out.println("Error while decrypting: " + e.toString());
        }
        return null;
    }


    private static void setKey(String myKey) {
        MessageDigest sha;
        try {
            key = myKey.getBytes(StandardCharsets.UTF_8);
            sha = MessageDigest.getInstance("SHA-256");
            key = sha.digest(key);
            key = Arrays.copyOf(key, 16);
            secretKey = new SecretKeySpec(key, "AES");
        } catch (NoSuchAlgorithmException e) {
          //  SathiLogger.e(e.getMessage());
            e.printStackTrace();
        }
    }


    private static SecretKeySpec setHDFCKey(String myKey) {
        MessageDigest sha;
        try {
            key = myKey.getBytes(StandardCharsets.UTF_8);
            sha = MessageDigest.getInstance("SHA-256");
            key = sha.digest(key);
            key = Arrays.copyOf(key, 16);
            secretKey = new SecretKeySpec(key, "AES");
        } catch (NoSuchAlgorithmException e) {
            //  SathiLogger.e(e.getMessage());
            e.printStackTrace();
        }
        return secretKey;
    }
    private static SecretKeySpec setPemKey(String myKey) {
        MessageDigest sha;
        try {
            key = myKey.getBytes(StandardCharsets.UTF_8);
            sha = MessageDigest.getInstance("SHA-256");
            key = sha.digest(key);
            key = Arrays.copyOf(key, 16);
            secretKey = new SecretKeySpec(key, "RSA");
        } catch (NoSuchAlgorithmException e) {
            //  SathiLogger.e(e.getMessage());
            e.printStackTrace();
        }
        return secretKey;
    }




    public static byte[] encrypt(SecretKeySpec key, byte[] content) {
        Cipher cipher;
        byte[] encrypted = null;
        try {
            cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, key);
            encrypted = cipher.doFinal(content);
        } catch (Exception e) {
            //SathiLogger.e(e.getMessage());
            e.printStackTrace();
        }
        return encrypted;
    }


    public static byte[] encryptHDFC(String key, byte[] content) {
        SecretKeySpec secretKeySpec = setHDFCKey(key);
        Cipher cipher;
        byte[] encrypted = null;
        try {
            cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
            encrypted = cipher.doFinal(content);
        } catch (Exception e) {
            //SathiLogger.e(e.getMessage());
            e.printStackTrace();
        }
        return encrypted;
    }


    public static byte[] encryptHDFCPem(String key, byte[] content) {
        SecretKeySpec secretKeySpec = setPemKey(key);
        Cipher cipher;
        byte[] encrypted = null;
        try {
            cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
            encrypted = cipher.doFinal(content);
            Log.e("encrypted val" , encrypted+"");
        } catch (Exception e) {
            //SathiLogger.e(e.getMessage());
            e.printStackTrace();
        }
        return encrypted;
    }


    private static byte[] decrypt(SecretKeySpec key, byte[] encryptedContent) {
        Cipher cipher;
        byte[] decrypted = null;
        try {
            cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, key);
            decrypted = cipher.doFinal(encryptedContent);
        } catch (Exception e) {
           // SathiLogger.e(e.getMessage());
            e.printStackTrace();
        }
        return decrypted;
    }

    private static void bytesToFile(String filePath, byte[] bytes) throws IOException {
        FileOutputStream fos = new FileOutputStream(filePath);
        fos.write(bytes);
        fos.close();
    }

    private static byte[] fileToBytes(String filePath) throws IOException {
        File f = new File(filePath);
        InputStream is = new FileInputStream(f);
        byte[] content = new byte[is.available()];
        is.read(content);
        return content;
    }

    public static byte[] convertImageToBase64(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
       // BitmapFactory.Options bmOptions = new BitmapFactory.Options();
//        Bitmap bitmap = BitmapFactory.decodeFile(path,bmOptions);
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos); //bm is the bitmap object
        byte[] byteArrayImage = baos.toByteArray();
        String encodedImage = Base64.encodeToString(byteArrayImage, Base64.DEFAULT);
        byte[] data = Base64.decode(encodedImage, Base64.DEFAULT);
        return data;
    }
}