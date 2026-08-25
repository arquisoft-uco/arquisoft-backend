package com.arquisoft.shared.tracing.infrastructure.traza.propagacion;

public final class TrazaHeaders {

    public static final String X_CORRELATION_ID = "X-Correlation-Id";
    public static final String X_TRANSACTION_ID = "X-Transaction-Id";
    public static final String TRACEPARENT = "traceparent";

    public static final String AMQP_TRACE_ID = "X-Trace-Id";
    public static final String AMQP_TRANSACTION_ID = "X-Transaction-Id";
    public static final String AMQP_USER_ID = "X-User-Id";

    private TrazaHeaders() {}
}
