package com.expensemanagement.splitshare.repository;

import com.expensemanagement.splitshare.entity.GroupsEntity;
import com.expensemanagement.splitshare.entity.TransactionsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionsRepository extends JpaRepository<TransactionsEntity, Long> {
    TransactionsEntity findByGroupId(Long userId);
    TransactionsEntity findByTransactionId(Long transactionId);
    TransactionsEntity findByPaymentId(Long paymentId);
}
