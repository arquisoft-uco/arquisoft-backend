package com.arquisoft.seguridad.infrastructure.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Filtro de Auditoría que registra todos los intentos de acceso a la API.
 */
@Slf4j
@Component
public class AuditFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, 
                                    FilterChain filterChain) throws ServletException, IOException {
        
        long startTime = System.currentTimeMillis();
        String clientIp = getClientIp(request);
        String method = request.getMethod();
        String requestUri = request.getRequestURI();
        
        try {
            filterChain.doFilter(request, response);
        } finally {
            long duration = System.currentTimeMillis() - startTime;
            String userId = extractUserId();
            int status = response.getStatus();
            
            if (!requestUri.contains("/actuator/health") && !requestUri.contains("/swagger")) {
                auditLog(clientIp, userId, method, requestUri, status, duration);
            }
        }
    }

    private String extractUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof Jwt jwt) {
            return jwt.getSubject();
        }
        return "ANONYMOUS";
    }

    private void auditLog(String clientIp, String userId, String method, String uri, int status, long duration) {
        if (status >= 200 && status < 300) {
            log.info("AUDIT [{}] {} {} - User: {} - Status: {} - Duration: {}ms",
                    clientIp, method, uri, userId, status, duration);
        } else if (status >= 400 && status < 500) {
            log.warn("AUDIT [{}] {} {} - User: {} - Status: {} - Duration: {}ms",
                    clientIp, method, uri, userId, status, duration);
        } else if (status >= 500) {
            log.error("AUDIT [{}] {} {} - User: {} - Status: {} - Duration: {}ms",
                    clientIp, method, uri, userId, status, duration);
        }
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty()) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty()) {
            ip = request.getRemoteAddr();
        }
        if (ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }
}
