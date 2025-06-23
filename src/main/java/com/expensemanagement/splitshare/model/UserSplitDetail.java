package com.expensemanagement.splitshare.model;

import java.util.Map;
import lombok.Data;

@Data
public class UserSplitDetail {

    private Map<Long, Double> splitMap;

}
