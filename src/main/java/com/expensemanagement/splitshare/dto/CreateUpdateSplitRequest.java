package com.expensemanagement.splitshare.dto;

import java.util.Map;
import lombok.Data;

@Data
public class CreateUpdateSplitRequest {
    private Long paymentId;
    private Long groupId;
    private Long userId;
    private String splitMethod;
    private Map<Long, Double> debtorSplitMap;
    private Map<Long, Double> payerSplitMap;
    private Double totalAmount;
}
