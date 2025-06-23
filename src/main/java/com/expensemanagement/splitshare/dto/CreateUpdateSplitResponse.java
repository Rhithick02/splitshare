package com.expensemanagement.splitshare.dto;

import java.util.List;
import java.util.Map;
import lombok.Data;
import org.apache.commons.lang3.tuple.Pair;

@Data
public class CreateUpdateSplitResponse {
    private Long userId;
    private Long groupId;
    private String groupName;
    private Long paymentId;
    private boolean isUpdatePayment;
    private Double paymentAmount;
    Map<Long, List<Pair<Long, Double>>> userPaymentTrack;
}
