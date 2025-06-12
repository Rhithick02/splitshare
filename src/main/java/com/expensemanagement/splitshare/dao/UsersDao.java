package com.expensemanagement.splitshare.dao;

import com.expensemanagement.splitshare.entity.UsersEntity;
import com.expensemanagement.splitshare.exception.NotFoundException;
import com.expensemanagement.splitshare.repository.UsersRepository;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class UsersDao {

    private final UsersRepository usersRepository;

    @Autowired
    public UsersDao(UsersRepository usersRepository) {
        this.usersRepository = usersRepository;
    }
    public UsersEntity getUserByPhoneNumber(String phoneNumber) {
        UsersEntity user = usersRepository.findByPhoneNumber(phoneNumber);
        if (Objects.isNull(user)) {
            log.error("No valid user is found for the phoneNumber - {}", phoneNumber);
            throw new NotFoundException("phoneNumber", phoneNumber);
        }
        log.info("Found UserId = {} with the phoneNumber = {}", user.getUserId(), phoneNumber);
        return user;
    }

    public boolean doesPhoneNumberExist(String phoneNumber) {
        UsersEntity user = usersRepository.findByPhoneNumber(phoneNumber);
        if (Objects.isNull(user)) {
            return false;
        }
        return true;
    }

    public UsersEntity upsertUserData(UsersEntity user) {
        UsersEntity userDataFromDb = null;
        if (Objects.nonNull(user.getPhoneNumber()) && doesPhoneNumberExist(user.getPhoneNumber())) {
            userDataFromDb = getUserByPhoneNumber(user.getPhoneNumber());
        }
        if (Objects.nonNull(userDataFromDb)) {
            if (Objects.isNull(user.getEmail())) {
                user.setEmail(userDataFromDb.getEmail());
            }
            if (Objects.isNull(user.getCountryCode())) {
                user.setCountryCode(userDataFromDb.getCountryCode());
            }
            if (Objects.isNull(user.getUserName())) {
                user.setUserName(userDataFromDb.getUserName());
            }
        }
        UsersEntity savedResponse = usersRepository.save(user);
        return savedResponse;
    }

    public UsersEntity getUserByUserId(Long userId) {
        UsersEntity user = usersRepository.findByUserId(userId);
        if (Objects.isNull(user)) {
            log.error("No valid user is found for the userId - {}", userId);
            throw new NotFoundException("userId", userId.toString());
        }
        log.info("Found UserId = {} with the userId = {}", user.getUserId(), userId);
        return user;
    }
}
