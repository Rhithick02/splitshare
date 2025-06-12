package com.expensemanagement.splitshare.validate.auth;

import com.expensemanagement.splitshare.exception.UnauthorizedException;
import com.expensemanagement.splitshare.validate.Validator;
import java.util.Objects;
import org.springframework.stereotype.Component;

@Component
public class AuthorizationValidator implements Validator {

    public void validate(Object request) {
        if (Objects.isNull(request)) {
            throw new UnauthorizedException();
        }
        if (request instanceof String) {
            String authHeader = (String) request;
            if (authHeader.isEmpty()
                    || !authHeader.startsWith("splitshare_token ")
                    || authHeader.split("splitshare_token ", -1).length != 2) {
                throw new UnauthorizedException();
            }
        }
    }
}
