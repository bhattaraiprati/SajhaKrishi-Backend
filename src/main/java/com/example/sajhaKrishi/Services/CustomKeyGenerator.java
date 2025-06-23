package com.example.sajhaKrishi.Services;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class CustomKeyGenerator {
    private static String secretKey = "";
    public static void main(String[] args) {
        try {
            KeyGenerator keyGen = KeyGenerator.getInstance("HmacSHA256");
            SecretKey sK = keyGen.generateKey();
            secretKey = Base64.getEncoder().encodeToString(sK.getEncoded());

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        System.out.println( secretKey);
    }
}
