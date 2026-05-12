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

    // m-4: patrón IPv4/IPv6 mínimo — previene log injection desde X-Forwarded-For
    private static final Pattern IP_PATTERN = Pattern.compile("^[\\d.:a-fA-F]{3,45}$");

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        // C-2 fix: userId se extrae antes del filterChain para que todos los logs
        // del request (use cases, servicios) incluyan el campo userId en MDC.
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

        Throwable thrown = null;
        try {
            filterChain.doFilter(request, response);
        } catch (Exception ex) {
            thrown = ex;  // m-3: capturar excepción para incluirla en log de 5xx
            throw ex;
        } finally {
            long duration = System.currentTimeMillis() - startTime;
            int status    = response.getStatus();
            // m-3: si Spring capturó la excepción internamente (500 sin throw), buscarla en atributo
            Throwable cause = thrown != null ? thrown
                    : (Throwable) request.getAttribute("jakarta.servlet.error.exception");

            if (!requestUri.contains("/actuator/health") && !requestUri.contains("/swagger")) {
                auditLog(clientIp, method, requestUri, status, duration, cause);
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

    // m-2: userId omitido del mensaje — ya está disponible como campo MDC estructurado en Grafana/Loki
    private void auditLog(String clientIp, String method, String uri, int status, long duration, Throwable cause) {
        if (status >= 200 && status < 300) {
            log.info("AUDIT [{}] {} {} - Status: {} - Duration: {}ms",
                    clientIp, method, uri, status, duration);
        } else if (status >= 400 && status < 500) {
            log.warn("AUDIT [{}] {} {} - Status: {} - Duration: {}ms",
                    clientIp, method, uri, status, duration);
        } else if (status >= 500) {
            // m-3: incluir excepción para diagnóstico desde Grafana sin revisar logs adicionales
            log.error("AUDIT [{}] {} {} - Status: {} - Duration: {}ms",
                    clientIp, method, uri, status, duration, cause);
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
        // m-4: validar formato IPv4/IPv6 para prevenir log injection desde headers manipulados
        return IP_PATTERN.matcher(ip).matches() ? ip : "INVALID";
    }
}
