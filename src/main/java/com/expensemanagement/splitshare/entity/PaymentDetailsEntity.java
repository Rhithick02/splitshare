package com.expensemanagement.splitshare.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "payment_details")
public class PaymentDetailsEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long paymentId;
    private Double amount;
    private String splitMethod;

    @OneToMany( mappedBy = "paymentDetail",
                cascade = CascadeType.ALL,
                fetch = FetchType.EAGER,
                orphanRemoval = true)
    private Set<SplitInformationEntity> split;

    @OneToMany( mappedBy = "paymentDetail",
                cascade = CascadeType.ALL,
                fetch = FetchType.EAGER,
                orphanRemoval = true)
    private Set<UserSplitPaymentsEntity> userSplitPayments;

    @ManyToOne
    @JoinColumn(name = "group_id")
    private GroupsEntity group;

    private Timestamp createDate;
    private Timestamp updateDate;

    @Transient
    private boolean isNew;

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

    public Long getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(Long paymentId) {
        this.paymentId = paymentId;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
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

    public Set<SplitInformationEntity> getSplit() {
        if (this.split == null) {
            this.split = new HashSet<>();
        }
        return split;
    }

    public void setSplit(Set<SplitInformationEntity> split) {
        this.split = split;
    }

    public String getSplitMethod() {
        return splitMethod;
    }

    public void setSplitMethod(String splitMethod) {
        this.splitMethod = splitMethod;
    }

    public GroupsEntity getGroup() {
        return group;
    }

    public void setGroup(GroupsEntity group) {
        this.group = group;
    }

    public Set<UserSplitPaymentsEntity> getUserSplitPayments() {
        return userSplitPayments;
    }

    public void setUserSplitPayments(Set<UserSplitPaymentsEntity> userSplitPayments) {
        this.userSplitPayments = userSplitPayments;
    }

    public boolean isNew() {
        return isNew;
    }

    public void setNew(boolean aNew) {
        isNew = aNew;
    }
}