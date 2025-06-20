package com.expensemanagement.splitshare.model;

import java.util.Map;
import lombok.Data;

@Data
public class SplitModel {

    private Map<Long, Double> splitMap;

}
