package com.expensemanagement.splitshare.dto;

import com.expensemanagement.splitshare.model.SplitModel;
import java.util.List;
import lombok.Data;

@Data
public class CreateUpdateSplitResponse {
    private Long userId;
    private Long paymentId;
    private Long groupId;
    private boolean isUpdate;
    private Double totalAmount;
}
