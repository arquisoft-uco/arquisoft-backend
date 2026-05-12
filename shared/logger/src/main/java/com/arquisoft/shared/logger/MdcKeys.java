package com.arquisoft.shared.logger;

/**
 * Constantes de clave MDC usadas en toda la plataforma.
 * Un cambio de nombre de clave solo requiere modificar esta clase.
 */
public final class MdcKeys {

    public static final String TRACE_ID = "traceId";
    public static final String USER_ID  = "userId";

    private MdcKeys() {}
}
