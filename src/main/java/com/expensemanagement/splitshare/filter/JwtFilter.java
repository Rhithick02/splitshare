package com.expensemanagement.splitshare.filter;

import com.expensemanagement.splitshare.constants.AuthConstants;
import com.expensemanagement.splitshare.exception.UnauthorizedException;
import com.expensemanagement.splitshare.util.HeaderUtil;
import com.expensemanagement.splitshare.util.JwtUtil;
import com.expensemanagement.splitshare.validate.Validator;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;


@Component
public class JwtFilter extends OncePerRequestFilter {

    private final Validator authorizationValidator;
    private final HeaderUtil headerUtil;
    private final JwtUtil jwtUtil;

    public JwtFilter(@Qualifier("authorizationValidator") Validator authorizationValidator, HeaderUtil headerUtil, JwtUtil jwtUtil) {
        this.authorizationValidator = authorizationValidator;
        this.headerUtil = headerUtil;
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String path = request.getRequestURI();
        for (String excludedPath : AuthConstants.JWT_VALIDATION_EXCLUDED_PATHS) {
            if (path.contains(excludedPath)) {
                filterChain.doFilter(request, response);
                return;
            }
        }
        try {
            String authorization = request.getHeader("Authorization");
            authorizationValidator.validate(authorization);
            Map<String, String> authParams = headerUtil.convertAuthorizationToParams(authorization);
            jwtUtil.decodeJWToken(authParams.get("token"), Long.parseLong(authParams.get("user_id")));
            filterChain.doFilter(request, response);
        } catch (UnauthorizedException ex) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid auth credentials");
        }
    }
}
