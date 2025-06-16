package com.expensemanagement.splitshare.repository;

import com.expensemanagement.splitshare.entity.PaymentDetailsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentsRepository extends JpaRepository<PaymentDetailsEntity, Long> {

}
