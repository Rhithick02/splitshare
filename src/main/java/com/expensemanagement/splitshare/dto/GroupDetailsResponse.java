package com.expensemanagement.splitshare.dto;

import com.expensemanagement.splitshare.model.GroupInformation;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;


public class GroupDetailsResponse {
    private List<GroupInformation> groupInformationList;

    public List<GroupInformation> getGroupInformationList() {
        if (this.groupInformationList == null) {
            this.groupInformationList = new ArrayList<>();
        }
        return groupInformationList;
    }

    public void setGroupInformationList(List<GroupInformation> groupInformationList) {
        this.groupInformationList = groupInformationList;
    }
}
