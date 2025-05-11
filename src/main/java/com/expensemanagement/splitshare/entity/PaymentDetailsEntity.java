//package com.expensemanagement.splitshare.entity;
//
//import jakarta.persistence.Entity;
//import jakarta.persistence.GeneratedValue;
//import jakarta.persistence.GenerationType;
//import jakarta.persistence.Id;
//import jakarta.persistence.Table;
//
//@Entity
//@Table(name = "payment_details")
//public class PaymentDetailsEntity {
//    @Id
//    @GeneratedValue(strategy = GenerationType.SEQUENCE)
//    private Long paymentId;
//
//    private Long payerId;
//    private Long payeeId;
//    private Long groupId;
//    private Double amount;
//    private String status;
//    private Boolean deleted;
//
//    public Long getPaymentId() {
//        return paymentId;
//    }
//
//    public void setPaymentId(Long paymentId) {
//        this.paymentId = paymentId;
//    }
//
//    public Long getPayerId() {
//        return payerId;
//    }
//
//    public void setPayerId(Long payerId) {
//        this.payerId = payerId;
//    }
//
//    public Long getPayeeId() {
//        return payeeId;
//    }
//
//    public void setPayeeId(Long payeeId) {
//        this.payeeId = payeeId;
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
//    public Double getAmount() {
//        return amount;
//    }
//
//    public void setAmount(Double amount) {
//        this.amount = amount;
//    }
//
//    public String getStatus() {
//        return status;
//    }
//
//    public void setStatus(String status) {
//        this.status = status;
//    }
//
//    public Boolean getDeleted() {
//        return deleted;
//    }
//
//    public void setDeleted(Boolean deleted) {
//        this.deleted = deleted;
//    }
//}