package com.arquisoft.shared.web.filter;

import com.arquisoft.shared.logger.MdcKeys;
import com.arquisoft.shared.util.UtilText;
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
import java.util.regex.Pattern;

/**
 * Filtro de correlación punta a punta: resuelve el ID de correlación de la request
 * y lo publica en el MDC y en la respuesta HTTP.
 *
 * <p>Resolución del ID (en orden):</p>
 * <ol>
 *   <li>Header {@link CorrelationHeaders#X_CORRELATION_ID} entrante, si es válido.</li>
 *   <li>trace-id del header {@link CorrelationHeaders#TRACEPARENT} (W3C Trace Context).</li>
 *   <li>UUID generado — cuando no llega correlación externa.</li>
 * </ol>
 *
 * <p>El ID resuelto se escribe siempre como header {@code X-Correlation-Id} de la
 * respuesta (antes de delegar en la cadena, para sobrevivir al commit de la respuesta),
 * de modo que el cliente pueda reportarlo y la transacción pueda reconstruirse desde
 * los logs. {@code GlobalAppExceptionHandler} lo incluye además como {@code traceId}
 * en el cuerpo de los errores.</p>
 *
 * <p>Orden -300 — corre ANTES de RateLimitingFilter y FilterChainProxy (Spring Security,
 * orden -100), garantizando traceId presente en TODOS los logs del hilo, incluyendo
 * requests rechazadas por rate-limit (429) o JWT inválido (401). AuditFilter
 * (Ordered.LOWEST_PRECEDENCE) es responsable del userId y del evento AUDIT.</p>
 */
@Component
@Order(-300)
public class TraceIdFilter extends OncePerRequestFilter {

    /** Whitelist anti log-injection para IDs de correlación externos. */
    private static final Pattern CORRELATION_ID_SEGURO = Pattern.compile("[A-Za-z0-9\\-]{1,64}");

    /** Formato W3C traceparent: version-traceid-parentid-flags. */
    private static final Pattern TRACEPARENT_RE =
            Pattern.compile("[0-9a-f]{2}-([0-9a-f]{32})-[0-9a-f]{16}-[0-9a-f]{2}");

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String traceId = resolverTraceId(request);
        MDC.put(MdcKeys.TRACE_ID, traceId);
        response.setHeader(CorrelationHeaders.X_CORRELATION_ID, traceId);
        try {
            filterChain.doFilter(request, response);
        } finally {
            MDC.remove(MdcKeys.TRACE_ID);
        }
    }

    private String resolverTraceId(HttpServletRequest request) {
        String correlacionEntrante = request.getHeader(CorrelationHeaders.X_CORRELATION_ID);
        if (esCorrelacionValida(correlacionEntrante)) {
            return correlacionEntrante;
        }

        String traceparent = request.getHeader(CorrelationHeaders.TRACEPARENT);
        if (!UtilText.isEmptyOrNull(traceparent)) {
            var matcher = TRACEPARENT_RE.matcher(traceparent);
            if (matcher.matches()) {
                return matcher.group(1);
            }
        }

        return UUID.randomUUID().toString();
    }

    private boolean esCorrelacionValida(String valor) {
        return !UtilText.isEmptyOrNull(valor) && CORRELATION_ID_SEGURO.matcher(valor).matches();
    }
}
