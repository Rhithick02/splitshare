package com.expensemanagement.splitshare.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import java.sql.Timestamp;
import java.util.Date;

@Entity
@Table(name = "user_split_payments")
public class UserSplitPaymentsEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator", allocationSize = 1)
    private Long userSplitPaymentId;

    private Long fromUserId;
    private Long toUserId;
    private Double amountOwed;
    private boolean outdated;
    private Long version;
    private Timestamp createDate;
    private Timestamp updateDate;

    @ManyToOne
    @JoinColumn(name = "payment_id")
    private PaymentDetailsEntity paymentDetail;

    @PrePersist
    public void setCreationAndUpdationDate() {
        Timestamp timestamp = new Timestamp(new Date().getTime());
        this.createDate = timestamp;
        this.updateDate = timestamp;
    }

    @PreUpdate
    public void setUpdationDate() {
        Timestamp timestamp = new Timestamp(new Date().getTime());
        this.updateDate = timestamp;
    }

    public Long getUserSplitPaymentId() {
        return userSplitPaymentId;
    }

    public void setUserSplitPaymentId(Long userSplitPaymentId) {
        this.userSplitPaymentId = userSplitPaymentId;
    }

    public Long getFromUserId() {
        return fromUserId;
    }

    public void setFromUserId(Long fromUserId) {
        this.fromUserId = fromUserId;
    }

    public Long getToUserId() {
        return toUserId;
    }

    public void setToUserId(Long toUserId) {
        this.toUserId = toUserId;
    }

    public Double getAmountOwed() {
        return amountOwed;
    }

    public void setAmountOwed(Double amountOwed) {
        this.amountOwed = amountOwed;
    }

    public Timestamp getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Timestamp createDate) {
        this.createDate = createDate;
    }

    public Timestamp getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Timestamp updateDate) {
        this.updateDate = updateDate;
    }

    public PaymentDetailsEntity getPayment() {
        return paymentDetail;
    }

    public void setPayment(PaymentDetailsEntity payment) {
        this.paymentDetail = payment;
    }

    public boolean isOutdated() {
        return outdated;
    }

    public void setOutdated(boolean outdated) {
        this.outdated = outdated;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }
}
