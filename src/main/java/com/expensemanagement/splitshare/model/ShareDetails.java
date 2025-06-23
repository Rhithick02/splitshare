package com.expensemanagement.splitshare.model;

import lombok.Data;

@Data
public class ShareDetails {
    private Long userId;
    private Long userName;
    private String relation; // Inflow OR Outflow w.r.t userId
    private Double amount;
}
