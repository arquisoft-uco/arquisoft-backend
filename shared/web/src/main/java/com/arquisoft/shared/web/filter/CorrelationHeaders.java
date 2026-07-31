package com.arquisoft.shared.web.filter;

/**
 * Headers HTTP de correlación soportados por {@link TraceIdFilter}.
 */
public final class CorrelationHeaders {

    private CorrelationHeaders() {}

    /** Header de correlación propio — se acepta entrante y se devuelve siempre en la respuesta. */
    public static final String X_CORRELATION_ID = "X-Correlation-Id";

    /** Header de trazabilidad W3C Trace Context — se acepta como fallback entrante. */
    public static final String TRACEPARENT = "traceparent";
}
