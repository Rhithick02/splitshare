package com.expensemanagement.splitshare.repository;

import com.expensemanagement.splitshare.entity.GroupsEntity;
import com.expensemanagement.splitshare.entity.UsersEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupsRepository extends JpaRepository<GroupsEntity, Long> {
    public GroupsEntity findByGroupId(Long userId);
    public GroupsEntity findByGroupName(String phoneNumber);
}
