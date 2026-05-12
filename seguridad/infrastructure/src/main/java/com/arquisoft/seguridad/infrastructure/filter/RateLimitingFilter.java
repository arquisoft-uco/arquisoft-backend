package com.arquisoft.seguridad.infrastructure.filter;

import com.arquisoft.seguridad.infrastructure.config.ratelimit.BucketResolver;
import com.arquisoft.shared.web.dto.ErrorResponseDTO;
import tools.jackson.databind.ObjectMapper;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.ConsumptionProbe;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

/**
 * Filtro de Rate Limiting que limita el número de solicitudes por IP.
 * Retorna 429 (Too Many Requests) si se excede el límite.
 */
@Slf4j
@Component
@Order(-200)
@RequiredArgsConstructor
public class RateLimitingFilter extends OncePerRequestFilter {

    private static final Pattern IP_PATTERN = Pattern.compile("^[\\d.:a-fA-F]{3,45}$");

    private static final String LOGIN_PATH = "/api/auth/login";

    private final BucketResolver bucketResolver;
    private final ObjectMapper objectMapper;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String uri = request.getRequestURI();
        return uri.startsWith("/api/swagger-ui")
                || uri.startsWith("/api/v3/api-docs")
                || uri.startsWith("/api/swagger-resources")
                || uri.startsWith("/api/actuator");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, 
                                    FilterChain filterChain) throws ServletException, IOException {
        
        if (!bucketResolver.isRateLimitEnabled()) {
            filterChain.doFilter(request, response);
            return;
        }

        // Los preflight OPTIONS no consumen cuota — son generados por el browser, no por el usuario
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            filterChain.doFilter(request, response);
            return;
        }

        String clientIp = getClientIp(request);
        
        boolean isLoginEndpoint = LOGIN_PATH.equals(request.getRequestURI());
        Bucket bucket = isLoginEndpoint ? 
                bucketResolver.resolveLoginBucket(clientIp) : 
                bucketResolver.resolveBucket(clientIp);

        ConsumptionProbe probe = bucket.tryConsumeAndReturnRemaining(1);

        if (probe.isConsumed()) {
            response.addHeader("X-Rate-Limit-Remaining", String.valueOf(probe.getRemainingTokens()));
            filterChain.doFilter(request, response);
        } else {
            long waitForRefill = TimeUnit.NANOSECONDS.toSeconds(probe.getNanosToWaitForRefill());

            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.setContentType("application/json;charset=UTF-8");
            response.addHeader("X-Rate-Limit-Retry-After-Seconds", String.valueOf(waitForRefill));

            ErrorResponseDTO body = ErrorResponseDTO.builder()
                    .error("Too Many Requests")
                    .errorCode("RATE_LIMIT_EXCEEDED")
                    .message("Has excedido el límite de solicitudes. Intenta de nuevo en " + waitForRefill + " segundos.")
                    .status(HttpStatus.TOO_MANY_REQUESTS.value())
                    .path(request.getRequestURI())
                    .build();
            objectMapper.writeValue(response.getWriter(), body);

            log.warn("Rate limit exceeded for IP: {} on endpoint: {}", clientIp, request.getRequestURI());
        }
    }

    /**
     * Extrae la IP del cliente usando request.getRemoteAddr().
     *
     * Con server.forward-headers-strategy=FRAMEWORK, Spring Boot registra
     * ForwardedHeaderFilter antes de cualquier filtro de aplicación. Ese filtro
     * procesa y elimina X-Forwarded-For / X-Real-IP reescribiendo getRemoteAddr()
     * con la IP real validada — leer los headers manualmente aquí sería código muerto
     * y omitiría la validación de proxy de Spring.
     */
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getRemoteAddr();
        return IP_PATTERN.matcher(ip).matches() ? ip : "INVALID";
    }
}
