package com.expensemanagement.splitshare.exception;

import lombok.Getter;

@Getter
public class NotFoundException extends RuntimeException {

    private final String resourceName;
    private final String resourceValue;

    public NotFoundException(String resourceName, String resourceValue) {
        super(String.format("Requested %s=%s is not found", resourceName, resourceValue));
        this.resourceName = resourceName;
        this.resourceValue = resourceValue;
    }
}
