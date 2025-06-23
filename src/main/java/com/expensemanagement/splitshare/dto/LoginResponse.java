package com.expensemanagement.splitshare.dto;

import com.expensemanagement.splitshare.model.GroupSummary;
import java.util.List;
import lombok.Data;

@Data
public class LoginResponse {
    private String accessToken;
    private String refreshToken;
    private Long userId;
    private String phoneNumber;
    private String emailId;
    private List<GroupSummary> groupSummaryList;

}
