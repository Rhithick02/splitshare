package com.expensemanagement.splitshare.validate.auth;

import com.expensemanagement.splitshare.dto.LoginRequest;
import com.expensemanagement.splitshare.exception.BadRequestException;
import com.expensemanagement.splitshare.exception.UnauthorizedException;
import com.expensemanagement.splitshare.validate.Validator;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import java.util.Objects;
import org.springframework.stereotype.Component;

@Component
public class LoginRequestValidator implements Validator {

    PhoneNumberUtil phoneNumberUtil = PhoneNumberUtil.getInstance();

    public void validate(Object request) {
        if (Objects.isNull(request)) {
            throw new BadRequestException("Missing login request");
        }
        if (request instanceof LoginRequest) {
            LoginRequest loginRequest = (LoginRequest) request;
            int countryCodeNumber = Integer.parseInt(loginRequest.getCountryCode().replace("+", ""));
            Long nationalNumber = Long.parseLong(loginRequest.getNationalNumber());
            validatePhoneNumber(countryCodeNumber, nationalNumber);
        }
    }

    private void validatePhoneNumber(int countryCodeNumber, Long nationalNumber) {
        Phonenumber.PhoneNumber phoneNumber = new Phonenumber.PhoneNumber();
        phoneNumber.setCountryCode(countryCodeNumber);
        phoneNumber.setNationalNumber(nationalNumber);

        if (phoneNumberUtil.isPossibleNumberForType(phoneNumber, PhoneNumberUtil.PhoneNumberType.MOBILE)) {
            return;
        }
        throw new BadRequestException("phoneNumber", phoneNumber.toString());
    }
}
