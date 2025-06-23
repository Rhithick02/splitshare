package com.expensemanagement.splitshare.util;

import com.expensemanagement.splitshare.exception.UnauthorizedException;
import java.util.HashMap;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class HeaderUtil {

    public Map<String, String> convertAuthorizationToParams(String authorization) {
        Map<String, String> authParams = new HashMap<>();
        String authToken = authorization.split("splitshare_token ", -1)[1];
        String[] keyValues = authToken.split(",");
        for (String keyValue : keyValues) {
            String[] param = keyValue.strip().split("=");
            if (param.length == 2) {
                authParams.put(param[0], param[1]);
            }
        }
        if (authParams.size() != 2) {
            throw new UnauthorizedException();
        }
        return authParams;
    }
}
