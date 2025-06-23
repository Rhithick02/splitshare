package com.expensemanagement.splitshare.model;

import java.util.List;
import java.util.Map;
import lombok.Data;
import org.apache.commons.lang3.tuple.Pair;

@Data
public class GroupInformation {
    private Long groupId;
    private String groupName;
    private String groupLink;
    private Map<Long, List<Pair<Long, Double>>> debtorUserSplitDetails;
}
