package com.expensemanagement.splitshare.exception;


import lombok.Getter;

@Getter
public class BadRequestException extends RuntimeException {

    private String attributeName;
    private String attributeValue;

    public BadRequestException(String attributeName, String attributeValue) {
        super(String.format("Field %s=%s is not valid", attributeName, attributeValue));
        this.attributeName = attributeName;
        this.attributeValue = attributeValue;
    }

    public BadRequestException(String message) {
        super(message);
    }
}
