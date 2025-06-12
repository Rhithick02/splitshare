package com.expensemanagement.splitshare.mapper;

import com.expensemanagement.splitshare.dto.AddUserResponse;
import com.expensemanagement.splitshare.dto.CreateGroupResponse;
import com.expensemanagement.splitshare.dto.LoginResponse;
import com.expensemanagement.splitshare.entity.TransactionsEntity;
import com.expensemanagement.splitshare.enums.TransactionTypeEnum;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class TxnMapper {

    public List<TransactionsEntity> mapRegistration(LoginResponse loginResponse) {
        List<TransactionsEntity> transactionsEntityList = new ArrayList<>();
        TransactionsEntity transactionsEntity = new TransactionsEntity();
        transactionsEntity.setUserId(loginResponse.getUserId());
        transactionsEntity.setTransactionType(TransactionTypeEnum.REGISTER.toString());
        transactionsEntityList.add(transactionsEntity);
        return transactionsEntityList;
    }

    public List<TransactionsEntity> mapCreateGroup(CreateGroupResponse createGroupResponse) {
        List<TransactionsEntity> transactionsEntityList = new ArrayList<>();
        TransactionsEntity transactionsEntity = new TransactionsEntity();
        transactionsEntity.setUserId(createGroupResponse.getUserId());
        transactionsEntity.setGroupId(createGroupResponse.getGroupId());
        transactionsEntity.setTransactionType(TransactionTypeEnum.CREATE_GROUP.toString());
        transactionsEntityList.add(transactionsEntity);
        return transactionsEntityList;
    }

    public List<TransactionsEntity> mapAddUsers(AddUserResponse addUserResponse) {
        List<TransactionsEntity> transactionsEntityList = new ArrayList<>();
        Long groupId = addUserResponse.getGroupId();
        for (Long userId : addUserResponse.getUserIds()) {
            TransactionsEntity transactionsEntity = new TransactionsEntity();
            transactionsEntity.setUserId(userId);
            transactionsEntity.setGroupId(groupId);
            transactionsEntity.setTransactionType(TransactionTypeEnum.JOIN_GROUP.toString());
            transactionsEntityList.add(transactionsEntity);
        }
        return transactionsEntityList;
    }
}
