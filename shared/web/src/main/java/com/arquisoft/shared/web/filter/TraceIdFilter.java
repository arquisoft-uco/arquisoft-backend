package com.arquisoft.shared.web.filter;

import com.arquisoft.shared.logger.MdcKeys;
import com.arquisoft.shared.util.UtilTexto;
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

@Component
@Order(-300)
public class TraceIdFilter extends OncePerRequestFilter {

    private static final Pattern CORRELATION_ID_SEGURO = Pattern.compile("[A-Za-z0-9\\-]{1,64}");

    private static final Pattern TRACEPARENT_RE =
            Pattern.compile("[0-9a-f]{2}-([0-9a-f]{32})-[0-9a-f]{16}-[0-9a-f]{2}");

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String idTraza = resolverTraceId(request);
        MDC.put(MdcKeys.ID_TRAZA, idTraza);
        response.setHeader(CorrelationHeaders.X_CORRELATION_ID, idTraza);
        try {
            filterChain.doFilter(request, response);
        } finally {
            MDC.remove(MdcKeys.ID_TRAZA);
        }
    }

    private String resolverTraceId(HttpServletRequest request) {
        String correlacionEntrante = request.getHeader(CorrelationHeaders.X_CORRELATION_ID);
        if (esCorrelacionValida(correlacionEntrante)) {
            return correlacionEntrante;
        }

        String traceparent = request.getHeader(CorrelationHeaders.TRACEPARENT);
        if (!UtilTexto.esVacioONulo(traceparent)) {
            var matcher = TRACEPARENT_RE.matcher(traceparent);
            if (matcher.matches()) {
                return matcher.group(1);
            }
        }

        return UUID.randomUUID().toString();
    }

    private boolean esCorrelacionValida(String valor) {
        return !UtilTexto.esVacioONulo(valor) && CORRELATION_ID_SEGURO.matcher(valor).matches();
    }
}
