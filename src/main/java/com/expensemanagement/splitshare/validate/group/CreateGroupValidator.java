package com.expensemanagement.splitshare.validate.group;

import com.expensemanagement.splitshare.dto.CreateGroupRequest;
import com.expensemanagement.splitshare.exception.BadRequestException;
import com.expensemanagement.splitshare.validate.Validator;
import java.util.Objects;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Component
public class CreateGroupValidator implements Validator {

    public void validate(Object request) {
        if (Objects.isNull(request)) {
            throw new BadRequestException("Missing createGroup request");
        }
        String errorMsg = "Missing field %s in request";
        if (request instanceof CreateGroupRequest) {
            CreateGroupRequest createGroupRequest = (CreateGroupRequest) request;
            if (StringUtils.isEmpty(createGroupRequest.getGroupName())) {
                throw new BadRequestException(String.format(errorMsg, "groupName"));
            }
            if (Objects.isNull(createGroupRequest.getUserId())) {
                throw new BadRequestException(String.format(errorMsg, "userId"));
            }
        }
    }
}
