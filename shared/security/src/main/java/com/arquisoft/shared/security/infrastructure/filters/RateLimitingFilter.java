package com.arquisoft.shared.security.infrastructure.filters;

import com.arquisoft.shared.security.infrastructure.config.RateLimitConfig;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.ConsumptionProbe;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * Filtro de Rate Limiting que limita el número de solicitudes por IP.
 * Usa Bucket4j para gestionar los límites de solicitudes.
 * 
 * Retorna 429 (Too Many Requests) si se excede el límite.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RateLimitingFilter extends OncePerRequestFilter {
    
    private final RateLimitConfig rateLimitConfig;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, 
                                    FilterChain filterChain) throws ServletException, IOException {
        
        if (!rateLimitConfig.isRateLimitEnabled()) {
            filterChain.doFilter(request, response);
            return;
        }

        String clientIp = getClientIp(request);
        
        // Usar bucket específico para login
        boolean isLoginEndpoint = request.getRequestURI().contains("/auth/login");
        Bucket bucket = isLoginEndpoint ? 
                rateLimitConfig.resolveLoginBucket(clientIp) : 
                rateLimitConfig.resolveBucket(clientIp);

        ConsumptionProbe probe = bucket.tryConsumeAndReturnRemaining(1);

        if (probe.isConsumed()) {
            response.addHeader("X-Rate-Limit-Remaining", String.valueOf(probe.getRemainingTokens()));
            filterChain.doFilter(request, response);
        } else {
            long waitForRefill = TimeUnit.NANOSECONDS.toSeconds(probe.getRoundedSecondsToWait());
            
            response.addHeader("X-Rate-Limit-Retry-After-Seconds", String.valueOf(waitForRefill));
            response.sendError(
                    HttpStatus.TOO_MANY_REQUESTS.value(),
                    "Has excedido el límite de solicitudes. Intenta de nuevo en " + waitForRefill + " segundos."
            );
            
            log.warn("Rate limit exceeded for IP: {} on endpoint: {}", clientIp, request.getRequestURI());
        }
    }

    /**
     * Obtiene la IP del cliente considerando proxies y load balancers.
     */
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty()) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty()) {
            ip = request.getRemoteAddr();
        }
        
        // Si X-Forwarded-For contiene múltiples IPs, tomar la primera
        if (ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        
        return ip;
    }
}
