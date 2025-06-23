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
        if (request instanceof CreateUpdateSplitRequest) {
            CreateUpdateSplitRequest createUpdateSplitRequest = (CreateUpdateSplitRequest) request;
            Double payerAmount = 0.0;
            if (Objects.isNull(createUpdateSplitRequest.getPayerSplitMap())) {
                throw new BadRequestException("payerSplitModel is invalid");
            }
            payerAmount += createUpdateSplitRequest.getPayerSplitMap().values().stream().reduce(0.0, Double::sum);
            Double debtorAmount = 0.0;
            if (Objects.isNull(createUpdateSplitRequest.getDebtorSplitMap())) {
                throw new BadRequestException("debtorSplitModel is invalid");
            }
            debtorAmount += createUpdateSplitRequest.getDebtorSplitMap().values().stream().reduce(0.0, Double::sum);
            if (!payerAmount.equals(debtorAmount) || !payerAmount.equals(createUpdateSplitRequest.getTotalAmount())) {
                throw new BadRequestException("Payment split does not match the total amount");
            }
        }
    }
}
