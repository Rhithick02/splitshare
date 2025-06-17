package com.expensemanagement.splitshare.validate.payment;

import com.expensemanagement.splitshare.dto.AddUserRequest;
import com.expensemanagement.splitshare.dto.CreateUpdateSplitRequest;
import com.expensemanagement.splitshare.exception.BadRequestException;
import com.expensemanagement.splitshare.validate.Validator;
import java.util.Objects;
import org.springframework.stereotype.Component;

@Component
public class AddUpdateSplitValidator implements Validator {
    public void validate(Object request) {
        if (Objects.isNull(request)) {
            throw new BadRequestException("Missing createGroup request");
        }
        String errorMsgMissingUser = "Missing userId %s in request";
        if (request instanceof CreateUpdateSplitRequest) {
            CreateUpdateSplitRequest createUpdateSplitRequest = (CreateUpdateSplitRequest) request;
            Double payerAmount = 0.0;
            for (Long payerUserId: createUpdateSplitRequest.getPayerUserIdList()) {
                if (Objects.isNull(createUpdateSplitRequest.getPayerSplitModel())
                        || Objects.isNull(createUpdateSplitRequest.getPayerSplitModel().getSplitMap())
                        || !createUpdateSplitRequest.getPayerSplitModel().getSplitMap().containsKey(payerUserId)) {
                    throw new BadRequestException(String.format(errorMsgMissingUser, payerUserId.toString()));
                }
                payerAmount += createUpdateSplitRequest.getPayerSplitModel().getSplitMap().get(payerUserId);
            }
            Double debtorAmount = 0.0;
            for (Long debtorUserId: createUpdateSplitRequest.getDebtorUserIdList()) {
                if (Objects.isNull(createUpdateSplitRequest.getDebtorSplitModel())
                        || Objects.isNull(createUpdateSplitRequest.getDebtorSplitModel().getSplitMap())
                        || !createUpdateSplitRequest.getDebtorSplitModel().getSplitMap().containsKey(debtorUserId)) {
                    throw new BadRequestException(String.format(errorMsgMissingUser, debtorUserId.toString()));
                }
                debtorAmount += createUpdateSplitRequest.getPayerSplitModel().getSplitMap().get(debtorUserId);
            }
            if (!payerAmount.equals(debtorAmount) || !payerAmount.equals(createUpdateSplitRequest.getTotalAmount())) {
                throw new BadRequestException("Payment split does not match the total amount");
            }
        }
    }
}
