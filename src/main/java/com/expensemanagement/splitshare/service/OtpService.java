package com.expensemanagement.splitshare.service;

import jakarta.annotation.PostConstruct;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import org.springframework.stereotype.Component;

@Component
public class OtpService {
    private final Random random = new SecureRandom();
    private static final Map<String, String> otpStorage = new HashMap<>();

    public String generateOtp(String phoneNumber) {
        String otp = String.format("%04d", random.nextInt(10000));
        otpStorage.put(phoneNumber, otp);
        return otp;
    }

    public boolean verifyOtp(String phoneNumber, String otp) {
        return otpStorage.containsKey(phoneNumber)
                && otpStorage.get(phoneNumber).equals(otp);
    }
}
