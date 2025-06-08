package com.expensemanagement.splitshare.constants;

import java.util.Arrays;
import java.util.List;

public class AuthConstants {
    public static final String JWT_SUBJECT = "User Details";
    public static final String JWT_ISSUER = "Splitshare";
    public static final String JWT_CLAIM_KEY_USER_ID = "userId";
    public static final String JWT_CLAIM_KEY_PHONE_NUMBER = "phoneNumber";
    public static final List<String> JWT_VALIDATION_EXCLUDED_PATHS = Arrays.asList("/v1/auth");
}
