//package com.expensemanagement.splitshare.entity;
//
//import jakarta.persistence.Entity;
//import jakarta.persistence.GeneratedValue;
//import jakarta.persistence.GenerationType;
//import jakarta.persistence.Id;
//import jakarta.persistence.Table;
//
//@Entity
//@Table(name = "transactions")
//public class TransactionsEntity {
//    @Id
//    @GeneratedValue(strategy = GenerationType.SEQUENCE)
//    private Long transactionId;
//
//    private Long groupId;
//    private Double totalAmount;
//    private String method;
//    private Long createdBy;
//    private Long updatedBy;
//
//    public Long getTransactionId() {
//        return transactionId;
//    }
//
//    public void setTransactionId(Long transactionId) {
//        this.transactionId = transactionId;
//    }
//
//    public Long getGroupId() {
//        return groupId;
//    }
//
//    public void setGroupId(Long groupId) {
//        this.groupId = groupId;
//    }
//
//    public Double getTotalAmount() {
//        return totalAmount;
//    }
//
//    public void setTotalAmount(Double totalAmount) {
//        this.totalAmount = totalAmount;
//    }
//
//    public String getMethod() {
//        return method;
//    }
//
//    public void setMethod(String method) {
//        this.method = method;
//    }
//
//    public Long getCreatedBy() {
//        return createdBy;
//    }
//
//    public void setCreatedBy(Long createdBy) {
//        this.createdBy = createdBy;
//    }
//
//    public Long getUpdatedBy() {
//        return updatedBy;
//    }
//
//    public void setUpdatedBy(Long updatedBy) {
//        this.updatedBy = updatedBy;
//    }
//}