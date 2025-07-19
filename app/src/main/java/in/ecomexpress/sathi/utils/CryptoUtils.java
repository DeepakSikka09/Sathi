package in.ecomexpress.sathi.utils;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.util.Base64;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
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
            return false;
        }
    }

    public static byte[] decryptFile1(String inFilePath ,String key) {
        try {
            setKey(key);
            File f = new File(inFilePath);
            InputStream is = new FileInputStream(f);
            byte[] content = new byte[is.available()];
            is.read(content);
            byte[] decryptedData = decrypt(secretKey, content);
            return decryptedData;
        } catch (Exception e) {
           e.printStackTrace();
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
            e.printStackTrace();
        }
    }

    @SuppressLint("GetInstance")
    public static byte[] encrypt(SecretKeySpec key, byte[] content) {
        Cipher cipher;
        byte[] encrypted = null;
        try {
            cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, key);
            encrypted = cipher.doFinal(content);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return encrypted;
    }

    @SuppressLint("GetInstance")
    private static byte[] decrypt(SecretKeySpec key, byte[] encryptedContent) {
        Cipher cipher;
        byte[] decrypted = null;
        try {
            cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, key);
            decrypted = cipher.doFinal(encryptedContent);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return decrypted;
    }

    public static byte[] convertImageToBase64(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos); //bm is the bitmap object
        byte[] byteArrayImage = baos.toByteArray();
        String encodedImage = Base64.encodeToString(byteArrayImage, Base64.DEFAULT);
        return Base64.decode(encodedImage, Base64.DEFAULT);
    }
}