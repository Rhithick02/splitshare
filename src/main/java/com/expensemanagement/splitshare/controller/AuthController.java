package com.expensemanagement.splitshare.controller;

import com.expensemanagement.splitshare.dto.LoginRequest;
import com.expensemanagement.splitshare.dto.LoginResponse;
import com.expensemanagement.splitshare.dto.RegisterRequest;
import com.expensemanagement.splitshare.dto.VerifyTokenRequest;
import com.expensemanagement.splitshare.integration.ImageKitIntegration;
import com.expensemanagement.splitshare.service.AuthService;
import com.expensemanagement.splitshare.validate.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/auth")
public class AuthController {

    private final Validator loginRequestValidator;
    private final Validator registerRequestValidator;
    private final AuthService authService;

    @Autowired
    public AuthController(@Qualifier("loginRequestValidator") Validator loginRequestValidator,
                          @Qualifier("registerRequestValidator") Validator registerRequestValidator,
                          AuthService authService) {
        this.loginRequestValidator = loginRequestValidator;
        this.registerRequestValidator = registerRequestValidator;
        this.authService = authService;
    }

    @PostMapping("/send-otp")
    public ResponseEntity<?> sendOtp(@RequestBody LoginRequest loginRequest) {
        loginRequestValidator.validate(loginRequest);
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
        loginRequestValidator.validate(loginRequest);
        LoginResponse loginResponse = authService.processLogin(loginRequest);
        return new ResponseEntity<>(loginResponse, HttpStatus.OK);
    }

    @PostMapping("/register")
    public ResponseEntity<LoginResponse> register(@RequestBody RegisterRequest registerRequest) {
        registerRequestValidator.validate(registerRequest);
        LoginResponse loginResponse = authService.processRegistration(registerRequest);
        return new ResponseEntity<>(loginResponse, HttpStatus.CREATED);
    }

    @PostMapping("/verify")
    public ResponseEntity<?> verifyToken(@RequestBody VerifyTokenRequest verifyTokenRequest) {
        authService.verifyToken(verifyTokenRequest.getAccessToken(), verifyTokenRequest.getUserId(), verifyTokenRequest.getPhoneNumber());
        return new ResponseEntity<>(HttpStatus.OK);
    }

//    @PostMapping("test")
//    public ResponseEntity<?> test() {
//        imageKitIntegration.uploadImage();
//        return new ResponseEntity<>(HttpStatus.CREATED);
//    }
}
