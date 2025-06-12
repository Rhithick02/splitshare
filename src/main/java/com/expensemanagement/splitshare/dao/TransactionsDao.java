package com.expensemanagement.splitshare.dao;

import com.expensemanagement.splitshare.dto.CreateGroupResponse;
import com.expensemanagement.splitshare.dto.LoginResponse;
import com.expensemanagement.splitshare.entity.TransactionsEntity;
import com.expensemanagement.splitshare.mapper.TxnMapper;
import com.expensemanagement.splitshare.repository.TransactionsRepository;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class TransactionsDao {

    private final TransactionsRepository transactionsRepository;
    private final TxnMapper txnMapper;

    @Autowired
    public TransactionsDao(TransactionsRepository transactionsRepository, TxnMapper txnMapper) {
        this.transactionsRepository = transactionsRepository;
        this.txnMapper = txnMapper;
    }

    public void populateTransactionHistory(Object entity) {
        TransactionsEntity transactionsEntity = null;
        if (entity instanceof LoginResponse) {
            transactionsEntity = txnMapper.mapRegistration((LoginResponse) entity);
        } else if (entity instanceof CreateGroupResponse) {
            transactionsEntity = txnMapper.mapCreateGroup((CreateGroupResponse) entity);
        }

        if (Objects.nonNull(transactionsEntity)) {
            transactionsRepository.save(transactionsEntity);
        }
    }
}
