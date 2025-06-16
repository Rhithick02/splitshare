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
    private Long groupId;
    private Double amount;
    private String status;
    private Boolean deleted;
    private String payerMethod;
    private String splitMethod;

    @OneToMany( mappedBy = "paymentDetail",
                cascade = CascadeType.ALL,
                fetch = FetchType.EAGER,
                orphanRemoval = true)
    private Set<SplitInformationEntity> split;
    private Timestamp createDate;
    private Timestamp updateDate;

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

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
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

    public String getPayerMethod() {
        return payerMethod;
    }

    public void setPayerMethod(String payerMethod) {
        this.payerMethod = payerMethod;
    }

    public String getSplitMethod() {
        return splitMethod;
    }

    public void setSplitMethod(String splitMethod) {
        this.splitMethod = splitMethod;
    }
}