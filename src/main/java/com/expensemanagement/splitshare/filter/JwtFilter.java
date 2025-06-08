package com.expensemanagement.splitshare.filter;

import com.expensemanagement.splitshare.constants.AuthConstants;
import com.expensemanagement.splitshare.exception.UnauthorizedException;
import com.expensemanagement.splitshare.util.HeaderUtil;
import com.expensemanagement.splitshare.util.JwtUtil;
import com.expensemanagement.splitshare.util.ValidateUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;


@Component
public class JwtFilter extends OncePerRequestFilter {

    private final ValidateUtil validateUtil;
    private final HeaderUtil headerUtil;
    private final JwtUtil jwtUtil;

    public JwtFilter(ValidateUtil validateUtil, HeaderUtil headerUtil, JwtUtil jwtUtil) {
        this.validateUtil = validateUtil;
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
            validateUtil.validateAuthHeader(authorization);
            Map<String, String> authParams = headerUtil.convertAuthorizationToParams(authorization);
            jwtUtil.decodeJWToken(authParams.get("token"), Long.parseLong(authParams.get("user_id")), authParams.get("phone_number"));
            filterChain.doFilter(request, response);
        } catch (UnauthorizedException ex) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid auth credentials");
        }
    }
}
