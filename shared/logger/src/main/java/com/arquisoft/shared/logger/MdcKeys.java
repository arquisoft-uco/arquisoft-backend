package com.arquisoft.shared.logger;

public final class MdcKeys {

    public static final String ID_TRAZA = "idTraza";
    public static final String ID_USUARIO = "idUsuario";

    // Campos de auditoría HTTP — puestos en MDC por AuditFilter y exportados como
    // campos JSON independientes por LogstashEncoder (consultables en Loki con | json).
    public static final String METODO_HTTP = "metodoHttp";
    public static final String ESTADO_HTTP = "estadoHttp";
    public static final String URI_HTTP = "uriHttp";
    public static final String DURACION_MS = "duracionMs";
    public static final String IP_CLIENTE = "ipCliente";

    private MdcKeys() {}
}
