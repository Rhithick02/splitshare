package com.expensemanagement.splitshare.util;

import com.expensemanagement.splitshare.dto.LoginRequest;
import com.expensemanagement.splitshare.exception.BadRequestException;
import com.expensemanagement.splitshare.exception.UnauthorizedException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import java.util.Objects;
import org.springframework.stereotype.Component;

@Component
public class ValidateUtil {

    PhoneNumberUtil phoneNumberUtil = PhoneNumberUtil.getInstance();

    public void validateAuthHeader(String authHeader) {
        if (Objects.isNull(authHeader)
                || authHeader.isEmpty()
                || !authHeader.startsWith("splitshare_token ")
                || authHeader.split("splitshare_token ", -1).length != 2) {
            throw new UnauthorizedException();
        }
    }
    public void validateLoginRequest(LoginRequest loginRequest) {
        // Validate phoneNumber
        int countryCodeNumber = Integer.parseInt(loginRequest.getCountryCode().replace("+", ""));
        Phonenumber.PhoneNumber phoneNumber = new Phonenumber.PhoneNumber();
        phoneNumber.setCountryCode(countryCodeNumber);
        phoneNumber.setNationalNumber(Long.parseLong(loginRequest.getNationalNumber()));

        if (phoneNumberUtil.isPossibleNumberForType(phoneNumber, PhoneNumberUtil.PhoneNumberType.MOBILE)) {
            return;
        }
        throw new BadRequestException("phoneNumber", phoneNumber.toString());
    }
}
