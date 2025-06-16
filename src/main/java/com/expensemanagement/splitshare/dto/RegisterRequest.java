package com.expensemanagement.splitshare.dto;

import lombok.Data;

@Data
public class RegisterRequest {
    private String userName;
    private String emailId;
    private String nationalNumber;
    private String countryCode;
    private String phoneOtp;
    private String emailOtp;
}
