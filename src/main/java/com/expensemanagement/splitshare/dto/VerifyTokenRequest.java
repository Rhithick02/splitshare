package com.expensemanagement.splitshare.dto;

import lombok.Data;

@Data
public class VerifyTokenRequest {
    private String accessToken;
    private String phoneNumber;
    private Long userId;
}
