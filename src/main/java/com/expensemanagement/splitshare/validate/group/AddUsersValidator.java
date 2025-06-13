package com.expensemanagement.splitshare.validate.group;

import com.expensemanagement.splitshare.dto.AddUserRequest;
import com.expensemanagement.splitshare.exception.BadRequestException;
import com.expensemanagement.splitshare.validate.Validator;
import java.util.Objects;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Component
public class AddUsersValidator implements Validator {
    public void validate(Object request) {
        if (Objects.isNull(request)) {
            throw new BadRequestException("Missing createGroup request");
        }
        String errorMsg = "Missing field %s in request";
        if (request instanceof AddUserRequest) {
            AddUserRequest addUserRequest = (AddUserRequest) request;
            if (Objects.isNull(addUserRequest.getGroupId())) {
                throw new BadRequestException(String.format(errorMsg, "groupId"));
            }
            Optional<String> invalidPhoneNumbers = addUserRequest
                    .getPhoneNumbers()
                    .stream()
                    .filter(phoneNumber -> Objects.isNull(phoneNumber) || StringUtils.isEmpty(phoneNumber))
                    .findAny();
            if (invalidPhoneNumbers.isPresent()) {
                throw new BadRequestException("Phone numbers cannot be empty");
            }
        }
    }
}
