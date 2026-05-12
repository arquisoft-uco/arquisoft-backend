package com.arquisoft.seguridad.infrastructure.filter;

import com.arquisoft.shared.logger.MdcKeys;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
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
import java.util.UUID;
import java.util.regex.Pattern;

/**
 * Filtro de Auditoría que registra todos los intentos de acceso a la API.
 */
@Slf4j
@Component
public class AuditFilter extends OncePerRequestFilter {

    private static final Pattern IP_PATTERN = Pattern.compile("^[\\d.:a-fA-F]{3,45}$");

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String userId = extractUserId();
        MDC.put(MdcKeys.USER_ID, userId);

        // Usa el traceId del proveedor activo (Brave/OTel via MDCScopeDecorator) si ya está en MDC.
        // Si no hay proveedor configurado, genera un traceId propio para correlación en logs.
        // Nombre de clave = estándar Brave/OTel → sin acoplamiento a ninguna librería.
        boolean providerActive = MDC.get(MdcKeys.TRACE_ID) != null;
        if (!providerActive) {
            MDC.put(MdcKeys.TRACE_ID, UUID.randomUUID().toString().replace("-", ""));
        }

        long startTime    = System.currentTimeMillis();
        String clientIp   = getClientIp(request);
        String method     = request.getMethod();
        String requestUri = request.getRequestURI();

        try {
            filterChain.doFilter(request, response);
        } finally {
            long duration = System.currentTimeMillis() - startTime;
            int status    = response.getStatus();

            if (!requestUri.contains("/actuator/health") && !requestUri.contains("/swagger")) {
                auditLog(clientIp, method, requestUri, status, duration);
            }

            // Solo limpiar lo que este filtro puso — si el proveedor de tracing lo gestionó,
            // él mismo cerrará el scope y limpiará traceId
            if (!providerActive) {
                MDC.remove(MdcKeys.TRACE_ID);
            }
            MDC.remove(MdcKeys.USER_ID);
        }
    }

    private String extractUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof Jwt jwt) {
            return jwt.getSubject();
        }
        return "ANONYMOUS";
    }

    private void auditLog(String clientIp, String method, String uri, int status, long duration) {
        if (status >= 200 && status < 300) {
            log.info("AUDIT [{}] {} {} - Status: {} - Duration: {}ms",
                    clientIp, method, uri, status, duration);
        } else if (status >= 400 && status < 500) {
            log.warn("AUDIT [{}] {} {} - Status: {} - Duration: {}ms",
                    clientIp, method, uri, status, duration);
        } else if (status >= 500) {
            log.error("AUDIT [{}] {} {} - Status: {} - Duration: {}ms",
                    clientIp, method, uri, status, duration);
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
        return IP_PATTERN.matcher(ip).matches() ? ip : "INVALID";
    }
}
