package com.expensemanagement.splitshare.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ErrorDetails {
    private String errorCode;
    private String message;
    private String details;
}
