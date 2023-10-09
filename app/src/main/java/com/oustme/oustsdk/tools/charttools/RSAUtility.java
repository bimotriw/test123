package com.oustme.oustsdk.tools.charttools;

import android.util.Base64;

import com.oustme.oustsdk.tools.OustSdkTools;

import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;


/**
 * Created by shilpysamaddar on 03/04/17.
 */

public class RSAUtility {
    public static String encrypt(String plainText, String key){
        try{
            PublicKey publicKey = KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(Base64.decode(key, Base64.DEFAULT)));
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            return Base64.encodeToString(cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8)),Base64.DEFAULT);
        }catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
        return null;
    }
}
