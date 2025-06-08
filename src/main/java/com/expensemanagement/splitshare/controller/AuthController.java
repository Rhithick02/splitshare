package com.expensemanagement.splitshare.controller;

import com.expensemanagement.splitshare.dto.LoginRequest;
import com.expensemanagement.splitshare.dto.LoginResponse;
import com.expensemanagement.splitshare.dto.VerifyTokenRequest;
import com.expensemanagement.splitshare.service.AuthService;
import com.expensemanagement.splitshare.util.ValidateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/auth")
public class AuthController {

    private final ValidateUtil validateUtil;
    private final AuthService authService;

    @Autowired
    public AuthController(ValidateUtil validateUtil, AuthService authService) {
        this.validateUtil = validateUtil;
        this.authService = authService;
    }

    @PostMapping("/send-otp")
    public ResponseEntity<?> sendOtp(@RequestBody LoginRequest loginRequest) {
        validateUtil.validateLoginRequest(loginRequest);
        authService.sendSms(loginRequest);
        return ResponseEntity.noContent().build();
    }

    /**
     * Validates the request, processes the login
     * @param loginRequest
     * @return
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
        validateUtil.validateLoginRequest(loginRequest);
        LoginResponse loginResponse = authService.processLogin(loginRequest);
        return new ResponseEntity<>(loginResponse, HttpStatus.OK);
    }

    @PostMapping("/verify")
    public ResponseEntity<?> verifyToken(@RequestBody VerifyTokenRequest verifyTokenRequest) {
        authService.verifyToken(verifyTokenRequest.getAccessToken(), verifyTokenRequest.getUserId(), verifyTokenRequest.getPhoneNumber());
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
