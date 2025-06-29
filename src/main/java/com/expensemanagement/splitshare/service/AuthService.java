package com.expensemanagement.splitshare.service;

import com.expensemanagement.splitshare.dao.OtpDao;
import com.expensemanagement.splitshare.dao.UsersDao;
import com.expensemanagement.splitshare.dto.LoginRequest;
import com.expensemanagement.splitshare.dto.LoginResponse;
import com.expensemanagement.splitshare.dto.RegisterRequest;
import com.expensemanagement.splitshare.entity.GroupsEntity;
import com.expensemanagement.splitshare.entity.OtpEntity;
import com.expensemanagement.splitshare.entity.UsersEntity;
import com.expensemanagement.splitshare.exception.BadRequestException;
import com.expensemanagement.splitshare.exception.InternalServerException;
import com.expensemanagement.splitshare.exception.NotFoundException;
import com.expensemanagement.splitshare.integration.TwilioSmsIntegration;
import com.expensemanagement.splitshare.model.GroupSummary;
import com.expensemanagement.splitshare.util.JwtUtil;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AuthService {

    private final UsersDao usersDao;
    private final OtpService otpService;
    private final OtpDao otpDao;
    private final TwilioSmsIntegration twilioSmsIntegration;
    private final JwtUtil jwtUtil;
    private final Environment environment;

    @Autowired
    public AuthService(UsersDao usersDao, OtpService otpService,
                       TwilioSmsIntegration twilioSmsIntegration,
                       JwtUtil jwtUtil, OtpDao otpDao,
                       Environment environment) {
        this.usersDao = usersDao;
        this.otpService = otpService;
        this.twilioSmsIntegration = twilioSmsIntegration;
        this.jwtUtil = jwtUtil;
        this.otpDao = otpDao;
        this.environment = environment;
    }

    public void sendSms(LoginRequest loginRequest) {
        String nationalNumber = loginRequest.getNationalNumber();
        String countryCode = loginRequest.getCountryCode();
        String phoneNumber = countryCode + nationalNumber;
        boolean doesPhoneNumberExist = usersDao.doesPhoneNumberExist(phoneNumber);
        if ((loginRequest.isRegistrationRequest() && doesPhoneNumberExist)) {
            throw new BadRequestException(String.format("Phonenumber %s already exists", phoneNumber));
        }
        if (!loginRequest.isRegistrationRequest() && !doesPhoneNumberExist) {
            throw new NotFoundException("phoneNumber", phoneNumber);
        }
        // Generate and send Otp
        Pair<String, String> otps = otpService.generateOtp(phoneNumber, loginRequest.getEmailId());
        twilioSmsIntegration.sendSms(phoneNumber, otps.getLeft());
        if (loginRequest.isRegistrationRequest()) {
            // send email otp
        }
    }

    public LoginResponse processLogin(LoginRequest loginRequest) {
        LoginResponse loginResponse = new LoginResponse();
        String nationalNumber = loginRequest.getNationalNumber();
        String countryCode = loginRequest.getCountryCode();
        String phoneNumber = countryCode + nationalNumber;
        // Check if user has registered
        UsersEntity user = usersDao.getUserByPhoneNumber(phoneNumber);
        if (Arrays.stream(environment.getActiveProfiles()).noneMatch("dev"::equalsIgnoreCase)) {
            if (!otpService.verifyOtp(phoneNumber, loginRequest.getOtp())) {
                OtpEntity otpEntity = otpDao.getOtpRecordByPhoneNumber(phoneNumber);
                String otpDigest = otpService.getMD5HashValue(loginRequest.getOtp());
                if (!otpDigest.equalsIgnoreCase(otpEntity.getPhoneOtp())) {
                    // throw exception
                }
            }
        }
        String accessToken = jwtUtil.generateJwAccessToken(user.getUserId(), phoneNumber);

        loginResponse.setAccessToken(accessToken);
        loginResponse.setUserId(user.getUserId());
        loginResponse.setPhoneNumber(phoneNumber);
        loginResponse.setEmailId(user.getEmail());
        List<GroupSummary> groupSummaryList = new ArrayList<>();
        for (GroupsEntity group: user.getGroups()) {
            GroupSummary groupSummary = new GroupSummary();
            groupSummary.setGroupId(group.getGroupId());
            groupSummary.setGroupName(group.getGroupName());
            groupSummaryList.add(groupSummary);
        }
        loginResponse.setGroupSummaryList(groupSummaryList);
        return loginResponse;
    }

    public LoginResponse processRegistration(RegisterRequest registerRequest) {
        try {
            LoginResponse loginResponse = new LoginResponse();
            String nationalNumber = registerRequest.getNationalNumber();
            String countryCode = registerRequest.getCountryCode();
            String phoneNumber = countryCode + nationalNumber;
            if (usersDao.doesPhoneNumberExist(phoneNumber)) {
                throw new BadRequestException(String.format("Phonenumber %s already exists", phoneNumber));
            }
            if (Arrays.stream(environment.getActiveProfiles()).noneMatch("dev"::equalsIgnoreCase)) {
                if (!otpService.verifyOtp(phoneNumber, registerRequest.getPhoneOtp())) {
                    OtpEntity otpEntity = otpDao.getOtpRecordByPhoneNumber(phoneNumber);
                    String otpDigest = otpService.getMD5HashValue(registerRequest.getPhoneOtp());
                    if (!otpDigest.equalsIgnoreCase(otpEntity.getPhoneOtp())) {
                        // throw exception
                    }
                }
            }

            // Build userEntity request and Save to DB
            UsersEntity user = new UsersEntity();
            user.setUserName(registerRequest.getUserName());
            user.setCountryCode(registerRequest.getCountryCode());
            user.setPhoneNumber(phoneNumber);
            user.setEmail(registerRequest.getEmailId());
            UsersEntity savedUser = usersDao.upsertUserData(user);

            // Generate loginResponse
            String accessToken = jwtUtil.generateJwAccessToken(savedUser.getUserId(), phoneNumber);
            loginResponse.setAccessToken(accessToken);
            loginResponse.setUserId(savedUser.getUserId());
            loginResponse.setPhoneNumber(phoneNumber);
            loginResponse.setEmailId(savedUser.getEmail());
            return loginResponse;
        } catch (SQLException ex) {
            throw new InternalServerException(ex.getMessage());
        }
    }

    public void verifyToken(String jwt, Long userId) {
        jwtUtil.decodeJWToken(jwt, userId);
    }
}
