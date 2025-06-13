package com.expensemanagement.splitshare.dto;

import lombok.Data;

@Data
public class CreateGroupResponse {
    private String groupName;
    private Long groupId;
    private Long userId;
    private String groupLink;
}
