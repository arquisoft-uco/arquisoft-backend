package com.arquisoft.seguridad.infrastructure.filter;

import com.arquisoft.seguridad.infrastructure.config.ratelimit.BucketResolver;
import com.arquisoft.shared.message.SeguridadMessages;
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
                    .error(SeguridadMessages.RateLimit.HTTP_TOO_MANY_REQUESTS)
                    .errorCode(SeguridadMessages.RateLimit.RATE_LIMIT_EXCEDIDO)
                    .message(SeguridadMessages.RateLimit.LIMITE_EXCEDIDO_PREFIJO + waitForRefill + SeguridadMessages.RateLimit.LIMITE_EXCEDIDO_SUFIJO)
                    .status(HttpStatus.TOO_MANY_REQUESTS.value())
                    .path(request.getRequestURI())
                    .build();
            objectMapper.writeValue(response.getWriter(), body);

            log.warn(SeguridadMessages.RateLimit.LOG_RATE_LIMIT_EXCEDIDO, clientIp, request.getRequestURI());
        }
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getRemoteAddr();
        return IP_PATTERN.matcher(ip).matches() ? ip : "INVALID";
    }
}
