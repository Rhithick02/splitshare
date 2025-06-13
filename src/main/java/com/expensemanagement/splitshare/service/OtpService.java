package com.expensemanagement.splitshare.service;

import com.expensemanagement.splitshare.dao.OtpDao;
import com.expensemanagement.splitshare.entity.OtpEntity;
import jakarta.annotation.PostConstruct;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class OtpService {
    private final Random random;
    private final OtpDao otpDao;
    private final MessageDigest md5Digest;
    private static final ConcurrentHashMap<String, String> otpStorage = new ConcurrentHashMap<>();

    @Autowired
    public OtpService(OtpDao otpDao) throws NoSuchAlgorithmException {
        this.random = new SecureRandom();
        this.otpDao = otpDao;
        this.md5Digest = MessageDigest.getInstance("MD5");
    }

    public Pair<String, String> generateOtp(String phoneNumber, String emailId) {
        String phoneOtp = String.format("%04d", random.nextInt(10000));
        String emailOtp = String.format("%04d", random.nextInt(10000));
        otpStorage.put(phoneNumber, phoneOtp);
        otpStorage.put(emailId, emailOtp);
        String phoneOtpDigest = convertBytesToString(md5Digest.digest(phoneOtp.getBytes()));
        String emailOtpDigest = convertBytesToString(md5Digest.digest(emailOtp.getBytes()));
        OtpEntity response = otpDao.upsertOtpRecord(phoneNumber, phoneOtpDigest, emailOtpDigest);
        return new ImmutablePair<>(phoneOtp, emailOtp);
    }

    public boolean verifyOtp(String phoneNumber, String otp) {
        return otpStorage.containsKey(phoneNumber)
                && otpStorage.get(phoneNumber).equals(otp);
    }

    public String getMD5HashValue(String message) {
        return convertBytesToString(md5Digest.digest(message.getBytes()));
    }

    private String convertBytesToString(byte[] byteArray) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : byteArray) {
            String hex = Integer.toHexString(0xFF & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }
}
