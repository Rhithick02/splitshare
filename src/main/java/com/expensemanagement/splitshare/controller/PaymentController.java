package com.expensemanagement.splitshare.controller;

import com.expensemanagement.splitshare.dao.TransactionsDao;
import com.expensemanagement.splitshare.dto.CreateUpdateSplitRequest;
import com.expensemanagement.splitshare.dto.CreateUpdateSplitResponse;
import com.expensemanagement.splitshare.dto.GetGroupTransactionsRequest;
import com.expensemanagement.splitshare.service.PaymentService;
import com.expensemanagement.splitshare.validate.Validator;
import jakarta.transaction.Transactional;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("v1/payments")
public class PaymentController {

    private final PaymentService paymentService;
    private final Validator addUpdateSplitValidator;
    private final TransactionsDao transactionsDao;

    @Autowired
    public PaymentController(@Qualifier("addUpdateSplitValidator") Validator addUpdateSplitValidator,
                             PaymentService paymentService, TransactionsDao transactionsDao) {
        this.paymentService = paymentService;
        this.addUpdateSplitValidator = addUpdateSplitValidator;
        this.transactionsDao = transactionsDao;
    }

    @PostMapping({"/add-split", "/update-split"})
    @Transactional
    public ResponseEntity<?> addUpdateSplit(@RequestBody CreateUpdateSplitRequest createUpdateSplitRequest, @RequestHeader Map<String, String> requestHeaders) {
        addUpdateSplitValidator.validate(createUpdateSplitRequest);
        CreateUpdateSplitResponse createUpdateSplitResponse = paymentService.createUpdateSplit(createUpdateSplitRequest);
        transactionsDao.populateTransactionHistory(createUpdateSplitResponse);
        return new ResponseEntity<>(createUpdateSplitResponse, HttpStatus.OK);
    }
}
