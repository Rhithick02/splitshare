package com.expensemanagement.splitshare.dto;

import java.util.Set;
import lombok.Data;

@Data
public class AddUserRequest {
    private Set<String> phoneNumbers;
    private Long groupId;
    private String groupLink;
}
