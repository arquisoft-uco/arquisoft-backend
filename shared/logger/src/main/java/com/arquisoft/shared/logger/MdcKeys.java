package com.arquisoft.shared.logger;

/**
 * Constantes de clave MDC usadas en toda la plataforma.
 *
 * <p>Centralizar los nombres aqui garantiza que AuditFilter, ChildSpanAspect
 * y cualquier componente futuro usen las mismas claves sin strings literales.
 * Un cambio de nombre solo requiere modificar esta clase.
 */
public final class MdcKeys {

    public static final String TRACE_ID = "traceId";
    public static final String USER_ID  = "userId";

    // SPAN_ID reservado: se activará cuando se configure un exporter Brave/OTel (Zipkin/Tempo).
    // Ver: .workspace/pendiente-amqp-traceid.md
    // public static final String SPAN_ID = "spanId";

    private MdcKeys() {
        // clase de utilidad — no instanciable
    }
}
