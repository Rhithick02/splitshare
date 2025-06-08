package com.expensemanagement.splitshare.util;

import com.expensemanagement.splitshare.dto.LoginRequest;
import com.expensemanagement.splitshare.dto.RegisterRequest;
import com.expensemanagement.splitshare.exception.BadRequestException;
import com.expensemanagement.splitshare.exception.UnauthorizedException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import java.util.Objects;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
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
        Long nationalNumber = Long.parseLong(loginRequest.getNationalNumber());
        validatePhoneNumber(countryCodeNumber, nationalNumber);
    }

    public void validateRegisterRequest(RegisterRequest registerRequest) {
        // Validate phoneNumber
        int countryCodeNumber = Integer.parseInt(registerRequest.getCountryCode().replace("+", ""));
        Long nationalNumber = Long.parseLong(registerRequest.getNationalNumber());
        validatePhoneNumber(countryCodeNumber, nationalNumber);
        validateEmailAddress(registerRequest.getEmailId());
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
    private void validateEmailAddress(String emailId) {
        try {
            InternetAddress emailAddress = new InternetAddress(emailId);
            emailAddress.validate();
        } catch (AddressException e) {
            throw new BadRequestException("emailId", emailId);
        }

    }
}
