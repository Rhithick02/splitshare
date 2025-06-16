package com.expensemanagement.splitshare.model;

import java.util.Map;
import lombok.Data;

@Data
public class SplitModel {
    private String splitMethod;
    private Map<Long, Double> splitMap;

}
