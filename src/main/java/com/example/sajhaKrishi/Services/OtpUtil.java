package com.example.sajhaKrishi.Services;

import java.security.SecureRandom;

public class OtpUtil {
    private static final SecureRandom random = new SecureRandom();

    public static String generateOtp() {
        int otp = 100000 + random.nextInt(900000); // Generates a number between 100000 and 999999
        return String.valueOf(otp);
    }
}
