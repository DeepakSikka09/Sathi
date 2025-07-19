package in.ecomexpress.sathi.ui.dummy.eds.eds_ekyc_idfc;

import org.apache.axis.encoding.Base64;

import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

public class JWT {
    private String privateKey;

    public JWT(String privateKey){
        this.privateKey = privateKey;
    }

    public String encode(KeyPair keyPair){
        String retStr = null;
        Claims claims = Jwts.claims();
        claims.put("jti", "1623863273000");
        claims.put("sub", "e92ce943-c6cf-46b7-8998-f42ce65b636c");
        claims.put("iss", "e92ce943-c6cf-46b7-8998-f42ce65b636c");
        claims.put("aud", "https://app.uat-opt.idfcfirstbank.com/platform/oauth/oauth2/token");
        claims.put("exp", "1654080528");
        // strip the headers
        privateKey = privateKey.replace("-----BEGIN RSA PRIVATE KEY-----", "");
        privateKey = privateKey.replace("-----END RSA PRIVATE KEY-----", "");
        privateKey = privateKey.replaceAll("\\s+", "");
        byte[] encodedKey = Base64.decode(this.privateKey);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(encodedKey);
        try{
            KeyFactory kf = KeyFactory.getInstance("RSA");
           // PrivateKey privKey = kf.generatePrivate(keySpec);
            retStr = Jwts.builder().setClaims(claims).signWith(keyPair.getPrivate()).compact();
        } catch(NoSuchAlgorithmException e){
            e.printStackTrace();
        } catch(Exception e){
            e.printStackTrace();
        }
        return retStr;
    }
}