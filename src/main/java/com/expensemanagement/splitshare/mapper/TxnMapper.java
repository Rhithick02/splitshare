package com.expensemanagement.splitshare.mapper;

import com.expensemanagement.splitshare.dto.CreateGroupResponse;
import com.expensemanagement.splitshare.dto.LoginResponse;
import com.expensemanagement.splitshare.entity.TransactionsEntity;
import com.expensemanagement.splitshare.enums.TransactionTypeEnum;
import org.springframework.stereotype.Component;

@Component
public class TxnMapper {

    public TransactionsEntity mapRegistration(LoginResponse loginResponse) {
        TransactionsEntity transactionsEntity = new TransactionsEntity();
        transactionsEntity.setUserId(loginResponse.getUserId());
        transactionsEntity.setTransactionType(TransactionTypeEnum.REGISTER.toString());
        return transactionsEntity;
    }

    public TransactionsEntity mapCreateGroup(CreateGroupResponse createGroupResponse) {
        TransactionsEntity transactionsEntity = new TransactionsEntity();
        transactionsEntity.setUserId(createGroupResponse.getUserId());
        transactionsEntity.setGroupId(createGroupResponse.getGroupId());
        transactionsEntity.setTransactionType(TransactionTypeEnum.CREATE_GROUP.toString());
        return transactionsEntity;
    }
}
