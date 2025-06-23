package com.expensemanagement.splitshare.model;

import lombok.Data;

@Data
public class GroupSummary {
    private Long groupId;
    private String groupName;
    private Double debtAmount;
    private Double lentAmount;
}
