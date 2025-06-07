package com.expensemanagement.splitshare.exception;


import lombok.Getter;

@Getter
public class BadRequestException extends RuntimeException {

    private final String attributeName;
    private final String attributeValue;

    public BadRequestException(String attributeName, String attributeValue) {
        super(String.format("Field %s=%s is not valid", attributeName, attributeValue));
        this.attributeName = attributeName;
        this.attributeValue = attributeValue;
    }
}
