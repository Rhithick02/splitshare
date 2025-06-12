package com.expensemanagement.splitshare.dto;

import java.util.HashSet;
import java.util.Set;
import lombok.Data;

@Data
public class AddUserResponse {
    private Long groupId;
    private Set<Long> userIds;

    public Set<Long> getUserIds() {
        if (this.userIds == null) {
            this.userIds = new HashSet<>();
        }
        return this.userIds;
    }
}
