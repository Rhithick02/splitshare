package com.expensemanagement.splitshare.dto;

import com.expensemanagement.splitshare.model.UserPaymentInformation;
import java.util.List;
import lombok.Data;

@Data
public class GetGroupTransactionsResponse {
    private Long groupId;
    private String groupName;
    private List<UserPaymentInformation> userPaymentInformation;
}
