package com.expensemanagement.splitshare.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.expensemanagement.splitshare.constants.AuthConstants;
import com.expensemanagement.splitshare.exception.UnauthorizedException;
import java.util.Calendar;
import java.util.Date;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class JwtUtil {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiry.in.days}")
    private Long jwtExpiryInDays;

    public String generateJwAccessToken(Long userId, String phoneNumber) {
        JWTCreator.Builder builder = JWT.create()
                .withSubject(AuthConstants.JWT_SUBJECT)
                .withIssuer(AuthConstants.JWT_ISSUER)
                .withIssuedAt(new Date())
                .withClaim(AuthConstants.JWT_CLAIM_KEY_USER_ID, userId)
                .withClaim(AuthConstants.JWT_CLAIM_KEY_PHONE_NUMBER, phoneNumber);

        Date currentDate = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentDate);
        calendar.add(Calendar.DATE, jwtExpiryInDays.intValue());
        Date futureDate = calendar.getTime();

        builder.withExpiresAt(futureDate);
        return builder.sign(Algorithm.HMAC256(jwtSecret));
    }

    public void decodeJWToken(String jwt, Long userId, String phoneNumber) {
        Algorithm algorithm = Algorithm.HMAC256(jwtSecret);
        JWTVerifier verifier = JWT.require(algorithm)
                .withIssuer(AuthConstants.JWT_ISSUER)
                .withClaim(AuthConstants.JWT_CLAIM_KEY_USER_ID, userId)
                .withClaim(AuthConstants.JWT_CLAIM_KEY_PHONE_NUMBER, phoneNumber)
                .build();
        try {
            verifier.verify(jwt);
        } catch (JWTVerificationException ex) {
            log.error("JWT verification failed for userId = {}", userId);
            throw new UnauthorizedException();
        }
    }
}
