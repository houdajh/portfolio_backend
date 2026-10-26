package com.devBrain.backend.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

@Component
public class AdminTokenInterceptor implements HandlerInterceptor {

    public static final String HEADER = "X-Admin-Token";

    private final byte[] adminToken;

    public AdminTokenInterceptor(@Value("${app.rag.admin-token}") String adminToken) {
        this.adminToken = adminToken.getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String provided = request.getHeader(HEADER);

        // A blank configured token rejects everything, so an empty env var can't match an empty header.
        if (adminToken.length > 0 && provided != null
                && MessageDigest.isEqual(adminToken, provided.getBytes(StandardCharsets.UTF_8))) {
            return true;
        }

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        return false;
    }
}
