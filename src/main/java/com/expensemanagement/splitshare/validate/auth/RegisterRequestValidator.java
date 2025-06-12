package com.expensemanagement.splitshare.validate.auth;

import com.expensemanagement.splitshare.dto.RegisterRequest;
import com.expensemanagement.splitshare.exception.BadRequestException;
import com.expensemanagement.splitshare.validate.Validator;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import java.util.Objects;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import org.springframework.stereotype.Component;

@Component
public class RegisterRequestValidator implements Validator {

    PhoneNumberUtil phoneNumberUtil = PhoneNumberUtil.getInstance();

    public void validate(Object request) {
        if (Objects.isNull(request)) {
            throw new BadRequestException("Missing register request");
        }
        if (request instanceof RegisterRequest) {
            RegisterRequest registerRequest = (RegisterRequest) request;
            // Validate phoneNumber
            int countryCodeNumber = Integer.parseInt(registerRequest.getCountryCode().replace("+", ""));
            Long nationalNumber = Long.parseLong(registerRequest.getNationalNumber());
            validatePhoneNumber(countryCodeNumber, nationalNumber);
            validateEmailAddress(registerRequest.getEmailId());
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

    private void validateEmailAddress(String emailId) {
        try {
            InternetAddress emailAddress = new InternetAddress(emailId);
            emailAddress.validate();
        } catch (AddressException e) {
            throw new BadRequestException("emailId", emailId);
        }

    }
}
