package com.expensemanagement.splitshare.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "split_information")
public class SplitInformationEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long splitId;
    private Long userId;
    private Double splitFraction;
    private Double amount;
    private String paymentParty; // Enum can be used for better type safety
    @ManyToOne
    @JoinColumn(name = "payment_id")
    private PaymentDetailsEntity paymentDetail;

    public Long getSplitId() {
        return splitId;
    }

    public void setSplitId(Long splitId) {
        this.splitId = splitId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Double getSplitFraction() {
        return splitFraction;
    }

    public void setSplitFraction(Double splitFraction) {
        this.splitFraction = splitFraction;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getPaymentParty() {
        return paymentParty;
    }

    public void setPaymentParty(String paymentParty) {
        this.paymentParty = paymentParty;
    }
}