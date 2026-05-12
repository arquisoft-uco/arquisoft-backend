package com.arquisoft.shared.logger;

/**
 * Constantes de clave MDC usadas en toda la plataforma.
 * Un cambio de nombre de clave solo requiere modificar esta clase.
 */
public final class MdcKeys {

    public static final String TRACE_ID    = "traceId";
    public static final String USER_ID     = "userId";

    // Campos de auditoría HTTP — puestos en MDC por AuditFilter y exportados como
    // campos JSON independientes por LogstashEncoder (consultables en Loki con | json).
    public static final String HTTP_METHOD = "httpMethod";
    public static final String HTTP_STATUS = "httpStatus";
    public static final String HTTP_URI    = "httpUri";
    public static final String DURATION_MS = "durationMs";
    public static final String CLIENT_IP   = "clientIp";

    private MdcKeys() {}
}
