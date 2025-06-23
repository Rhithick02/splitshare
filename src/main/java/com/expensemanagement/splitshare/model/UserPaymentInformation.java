package com.expensemanagement.splitshare.model;

import java.util.List;
import lombok.Data;

@Data
public class UserPaymentInformation {
    private Long userId;
    private String userName;
    private List<ShareDetails> shareDetailsList;
    private Double totalPendingOutflow;
    private Double totalPendingInflow;
}
