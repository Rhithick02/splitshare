package com.expensemanagement.splitshare.repository;

import com.expensemanagement.splitshare.entity.GroupsEntity;
import com.expensemanagement.splitshare.entity.OtpEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OtpRepository extends JpaRepository<OtpEntity, String> {
    OtpEntity findByPhoneNumber(String phoneNumber);
    OtpEntity deleteByPhoneNumber(String phoneNumber);
}
