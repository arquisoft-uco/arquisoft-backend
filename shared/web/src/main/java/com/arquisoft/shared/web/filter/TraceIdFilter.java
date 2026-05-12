package com.arquisoft.shared.web.filter;

import com.arquisoft.shared.logger.MdcKeys;
import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;

/**
 * Filtro que inyecta un traceId único en el MDC al inicio de cada request.
 *
 * Responsabilidad única: correlación de logs por request.
 *
 * Orden -300 — corre ANTES de RateLimitingFilter y FilterChainProxy (Spring Security, orden -100).
 * Esto garantiza que traceId esté presente en TODOS los logs del hilo,
 * incluyendo requests rechazados por rate-limit (429) o JWT inválido (401)
 * que nunca llegan a AuditFilter.
 *
 * AuditFilter (Ordered.LOWEST_PRECEDENCE) es responsable del userId y del
 * evento AUDIT; este filtro solo gestiona el traceId.
 */
@Component
@Order(-300)
public class TraceIdFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        MDC.put(MdcKeys.TRACE_ID, UUID.randomUUID().toString());
        try {
            filterChain.doFilter(request, response);
        } finally {
            MDC.remove(MdcKeys.TRACE_ID);
        }
    }
}
