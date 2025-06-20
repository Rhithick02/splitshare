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
@Table(name = "user_split_details")
public class UserSplitDetailsEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator", allocationSize = 1)
    private Long userSplitId;

    private Long fromUserId;
    private Long toUserId;
    private Double amountOwed;
    private Double amountPaid;
    private String paymentStatus;
    private Timestamp createDate;
    private Timestamp updateDate;

    @ManyToOne
    @JoinColumn(name = "group_id")
    private GroupsEntity group;

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

    public Long getUserSplitId() {
        return userSplitId;
    }

    public void setUserSplitId(Long userSplitId) {
        this.userSplitId = userSplitId;
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

    public Double getAmountPaid() {
        return amountPaid;
    }

    public void setAmountPaid(Double amountPaid) {
        this.amountPaid = amountPaid;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public GroupsEntity getGroup() {
        return group;
    }

    public void setGroup(GroupsEntity group) {
        this.group = group;
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
}
