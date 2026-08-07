package com.arquisoft.seguridad.infrastructure.filter;

import com.arquisoft.shared.logger.MdcKeys;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

@Slf4j
@Component
@Order(Ordered.LOWEST_PRECEDENCE)  // debe ejecutarse DESPUÉS de FilterChainProxy (orden -100)
public class AuditFilter extends OncePerRequestFilter {

    private static final Pattern IP_PATTERN = Pattern.compile("^[\\d.:a-fA-F]{3,45}$");

    private static final List<String> AUDIT_EXCLUDED_PREFIXES = List.of(
            "/api/actuator/",
            "/api/swagger-ui",
            "/api/v3/api-docs",
            "/api/swagger-resources"
    );

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        // Guardar estado MDC previo (contiene traceId de TraceIdFilter) para restaurar
        // atómicamente al finalizar — evita removes individuales y race conditions con VTs.
        Map<String, String> preMdc = MDC.getCopyOfContextMap();
        // traceId lo gestiona TraceIdFilter (orden -300, antes de LimitadorSolicitudesFilter y Spring Security).
        // AuditFilter solo es responsable de userId y del evento AUDIT.
        MDC.put(MdcKeys.USER_ID, extractUserId());

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
            }

            // Restaurar MDC al estado previo al filtro (preserva traceId, elimina userId y campos HTTP)
            if (preMdc != null) {
                MDC.setContextMap(preMdc);
            } else {
                MDC.clear();
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
        if (uri == null) {
            return "UNKNOWN";
        }
        return uri.replaceAll("[\r\n\t\0]", "_");
    }

    private String getClientIp(HttpServletRequest request) {
        // PII: la IP del cliente es dato personal bajo GDPR / Ley 1581.
        // Base legal: interés legítimo (seguridad, detección de abuso de la API).
        // Para mayor privacidad, considerar anonimizar el último octeto IPv4.
        // request.getRemoteAddr() ya refleja la IP real cuando server.forward-headers-strategy=FRAMEWORK
        // está configurado — Spring's ForwardedHeaderFilter valida el proxy antes de reescribir la IP.
        String ip = request.getRemoteAddr();
        return IP_PATTERN.matcher(ip).matches() ? ip : "INVALID";
    }
}
