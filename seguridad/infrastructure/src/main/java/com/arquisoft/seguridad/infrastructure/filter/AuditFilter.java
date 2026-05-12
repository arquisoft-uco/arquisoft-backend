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
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

/**
 * Filtro de Auditoría que registra todos los intentos de acceso a la API.
 */
@Slf4j
@Component
public class AuditFilter extends OncePerRequestFilter {

    private static final Pattern IP_PATTERN = Pattern.compile("^[\\d.:a-fA-F]{3,45}$");

    private static final List<String> AUDIT_EXCLUDED_PREFIXES = List.of(
            "/api/actuator/health",
            "/api/swagger-ui",
            "/api/v3/api-docs"
    );

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        // traceId y userId se ponen al inicio: propagan a TODOS los logs del hilo (correlación).
        MDC.put(MdcKeys.USER_ID,  extractUserId());
        MDC.put(MdcKeys.TRACE_ID, UUID.randomUUID().toString().replace("-", ""));

        // Capturamos los datos HTTP pero NO los ponemos en MDC todavía —
        // se agregarán solo en el evento AUDIT para no contaminar logs de capas internas.
        String clientIp   = getClientIp(request);
        String method     = request.getMethod();
        String requestUri = sanitizeUri(request.getRequestURI());
        long   startTime  = System.currentTimeMillis();

        try {
            filterChain.doFilter(request, response);
        } finally {
            long duration = System.currentTimeMillis() - startTime;
            int  status   = response.getStatus();

            if (!isExcluded(requestUri)) {
                // Campos HTTP en MDC solo durante la escritura del log AUDIT
                MDC.put(MdcKeys.HTTP_METHOD, method);
                MDC.put(MdcKeys.HTTP_URI,    requestUri);
                MDC.put(MdcKeys.HTTP_STATUS, String.valueOf(status));
                MDC.put(MdcKeys.DURATION_MS, String.valueOf(duration));
                MDC.put(MdcKeys.CLIENT_IP,   clientIp);
                auditLog(status);
                MDC.remove(MdcKeys.HTTP_METHOD);
                MDC.remove(MdcKeys.HTTP_URI);
                MDC.remove(MdcKeys.HTTP_STATUS);
                MDC.remove(MdcKeys.DURATION_MS);
                MDC.remove(MdcKeys.CLIENT_IP);
            }

            MDC.remove(MdcKeys.TRACE_ID);
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

    private void auditLog(int status) {
        if (status >= 200 && status < 300) {
            log.info("AUDIT");
        } else if (status >= 400 && status < 500) {
            log.warn("AUDIT");
        } else if (status >= 500) {
            log.error("AUDIT");
        }
    }

    private boolean isExcluded(String uri) {
        return AUDIT_EXCLUDED_PREFIXES.stream().anyMatch(uri::startsWith);
    }

    private static String sanitizeUri(String uri) {
        if (uri == null) return "UNKNOWN";
        return uri.replaceAll("[\r\n\t]", "_");
    }

    private String getClientIp(HttpServletRequest request) {
        // request.getRemoteAddr() ya refleja la IP real cuando server.forward-headers-strategy=FRAMEWORK
        // está configurado — Spring's ForwardedHeaderFilter valida el proxy antes de reescribir la IP.
        String ip = request.getRemoteAddr();
        return IP_PATTERN.matcher(ip).matches() ? ip : "INVALID";
    }
}
