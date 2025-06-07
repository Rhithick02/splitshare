package com.expensemanagement.splitshare.model;

import lombok.Data;

@Data
public class GroupInformation {
    private Long groupId;
    private String groupName;
    private Double debtAmount;
    private Double lentAmount;
}
