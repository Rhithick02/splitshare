package com.expensemanagement.splitshare.dao;

import com.expensemanagement.splitshare.entity.GroupsEntity;
import com.expensemanagement.splitshare.entity.OtpEntity;
import com.expensemanagement.splitshare.exception.BadRequestException;
import com.expensemanagement.splitshare.exception.NotFoundException;
import com.expensemanagement.splitshare.repository.OtpRepository;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class OtpDao {
    private final OtpRepository otpRepository;

    @Autowired
    public OtpDao(OtpRepository otpRepository) {
        this.otpRepository = otpRepository;
    }

    public OtpEntity upsertOtpRecord(String phoneNumber, String phoneOtpDigest, String emailOtpDigest) {
        OtpEntity otpEntity = otpRepository.findByPhoneNumber(phoneNumber);
        if (Objects.isNull(otpEntity)) {
            otpEntity = new OtpEntity();
            otpEntity.setPhoneNumber(phoneNumber);
        }
        if (StringUtils.isNotEmpty(phoneOtpDigest)) {
            otpEntity.setPhoneOtp(phoneOtpDigest);
        }
        if (StringUtils.isNotEmpty(emailOtpDigest)) {
            otpEntity.setEmailOtp(emailOtpDigest);
        }
        return otpRepository.save(otpEntity);
    }

    public boolean doesOtpRecordExist(String phoneNumber) {
        OtpEntity otpEntity = otpRepository.findByPhoneNumber(phoneNumber);
        if (Objects.isNull(otpEntity)) {
            return false;
        }
        return true;
    }

    public OtpEntity getOtpRecordByPhoneNumber(String phoneNumber) {
        OtpEntity otpEntity = otpRepository.findByPhoneNumber(phoneNumber);
        if (Objects.isNull(otpEntity)) {
            log.error("No valid otp record found for the phoneRecord - {}", phoneNumber);
            throw new NotFoundException("phoneNumber", phoneNumber);
        }
        return otpEntity;
    }
}
