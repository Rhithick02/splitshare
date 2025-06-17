package com.expensemanagement.splitshare.dto;

import com.expensemanagement.splitshare.model.SplitModel;
import java.util.List;
import lombok.Data;

@Data
public class CreateUpdateSplitRequest {
    private Long paymentId;
    private List<Long> payerUserIdList;
    private List<Long> debtorUserIdList;
    private Long groupId;
    private Long userId;
    private SplitModel debtorSplitModel;
    private SplitModel payerSplitModel;
    private Double totalAmount;
}
