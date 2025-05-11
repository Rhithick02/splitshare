package com.expensemanagement.splitshare.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

import java.sql.Timestamp;
import java.util.Date;
import java.util.Set;

@Entity
@Table(name = "groups")
public class GroupsEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long groupId;
    private String groupName;
    private Timestamp createDate;
    private Timestamp updateDate;

    @ManyToMany(mappedBy = "groups")
    private Set<UsersEntity> users;

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


    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public Set<UsersEntity> getUsers() {
        return users;
    }

    public void setUsers(Set<UsersEntity> users) {
        this.users = users;
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