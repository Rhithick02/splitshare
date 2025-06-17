package com.expensemanagement.splitshare.service;

import com.expensemanagement.splitshare.dao.PaymentsDao;
import com.expensemanagement.splitshare.dto.CreateUpdateSplitRequest;
import com.expensemanagement.splitshare.dto.CreateUpdateSplitResponse;
import com.expensemanagement.splitshare.entity.PaymentDetailsEntity;
import com.expensemanagement.splitshare.entity.SplitInformationEntity;
import com.expensemanagement.splitshare.enums.PaymentPartyEnum;
import com.expensemanagement.splitshare.enums.PaymentStatusEnum;
import com.expensemanagement.splitshare.exception.InternalServerException;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PaymentService {
    private final PaymentsDao paymentsDao;

    @Autowired
    public PaymentService(PaymentsDao paymentsDao) {
        this.paymentsDao = paymentsDao;
    }

    public CreateUpdateSplitResponse createOrUpdateSplit(CreateUpdateSplitRequest createUpdateSplitRequest) {
        try {
            CreateUpdateSplitResponse createUpdateSplitResponse = new CreateUpdateSplitResponse();
            PaymentDetailsEntity paymentDetailsEntity = new PaymentDetailsEntity();
            if (Objects.nonNull(createUpdateSplitRequest.getPaymentId())) {
                paymentDetailsEntity.setPaymentId(createUpdateSplitRequest.getPaymentId());
                createUpdateSplitResponse.setUpdate(true);
            }
            paymentDetailsEntity.setAmount(createUpdateSplitRequest.getTotalAmount());
            paymentDetailsEntity.setGroupId(createUpdateSplitRequest.getGroupId());
            paymentDetailsEntity.setStatus(PaymentStatusEnum.UNSETTLED.toString());
            paymentDetailsEntity.setPayerMethod(createUpdateSplitRequest.getPayerSplitModel().getSplitMethod());
            paymentDetailsEntity.setSplitMethod(createUpdateSplitRequest.getDebtorSplitModel().getSplitMethod());
            Set<SplitInformationEntity> splitInformationEntitySet = new HashSet<>();
            // PAYER
            for (Long payerUserId : createUpdateSplitRequest.getPayerUserIdList()) {
                SplitInformationEntity splitInformationEntity = new SplitInformationEntity();
                splitInformationEntity.setUserId(payerUserId);
                splitInformationEntity.setPaymentParty(PaymentPartyEnum.PAYER.toString());
                Double contribution = createUpdateSplitRequest.getPayerSplitModel().getSplitMap().get(payerUserId);
                Double fraction = contribution / createUpdateSplitRequest.getTotalAmount();
                splitInformationEntity.setSplitFraction(fraction);
                splitInformationEntity.setAmount(contribution);
                splitInformationEntitySet.add(splitInformationEntity);
            }

            // DEBTOR
            for (Long debtorUserId : createUpdateSplitRequest.getDebtorUserIdList()) {
                SplitInformationEntity splitInformationEntity = new SplitInformationEntity();
                splitInformationEntity.setUserId(debtorUserId);
                splitInformationEntity.setPaymentParty(PaymentPartyEnum.DEBTOR.toString());
                Double contribution = createUpdateSplitRequest.getPayerSplitModel().getSplitMap().get(debtorUserId);
                Double fraction = contribution / createUpdateSplitRequest.getTotalAmount();
                splitInformationEntity.setSplitFraction(fraction);
                splitInformationEntity.setAmount(contribution);
                splitInformationEntitySet.add(splitInformationEntity);
            }
            paymentDetailsEntity.setSplit(splitInformationEntitySet);
            PaymentDetailsEntity responseSavedToDb = paymentsDao.savePaymentDetail(paymentDetailsEntity);
            createUpdateSplitResponse.setPaymentId(responseSavedToDb.getPaymentId());
            createUpdateSplitResponse.setGroupId(responseSavedToDb.getGroupId());
            createUpdateSplitResponse.setUserId(createUpdateSplitResponse.getUserId());
            return createUpdateSplitResponse;
        } catch (SQLException ex) {
            throw new InternalServerException(ex.getMessage());
        }
    }
}
