package com.expensemanagement.splitshare.dao;

import com.expensemanagement.splitshare.entity.PaymentDetailsEntity;
import com.expensemanagement.splitshare.repository.PaymentsRepository;
import java.sql.SQLException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class PaymentsDao {
    private final PaymentsRepository paymentsRepository;

    @Autowired
    public PaymentsDao(PaymentsRepository paymentsRepository) {
        this.paymentsRepository = paymentsRepository;
    }

    public PaymentDetailsEntity savePaymentDetail(PaymentDetailsEntity paymentDetailsEntity) throws SQLException {
        try {
            return paymentsRepository.save(paymentDetailsEntity);
        } catch (Exception ex) {
            log.error("Exception occurred while trying to save the paymentDetailsEntity for groupId = {}. Exception - {}", paymentDetailsEntity.getGroup().getGroupId().toString(), ex.getMessage());
            throw new SQLException(ex.getMessage());
        }
    }
}
