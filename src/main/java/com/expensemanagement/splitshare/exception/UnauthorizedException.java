package com.expensemanagement.splitshare.exception;

public class UnauthorizedException extends RuntimeException {
    public UnauthorizedException() {
        super("Invalid auth credentials");
    }
}
