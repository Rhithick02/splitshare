package com.expensemanagement.splitshare.dto;

import lombok.Data;

@Data
public class LoginRequest {
    private String nationalNumber;
    private String emailId;
    private String countryCode;
    private String otp;
    private boolean isRegistrationRequest;
}
