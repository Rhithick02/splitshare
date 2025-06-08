package com.expensemanagement.splitshare.repository;

import com.expensemanagement.splitshare.entity.UsersEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsersRepository extends JpaRepository<UsersEntity, Long> {
    public UsersEntity findByUserId(Long userId);
    public UsersEntity findByPhoneNumber(String phoneNumber);
}
