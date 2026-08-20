package com.arquisoft.shared.tracing.infrastructure.traza.secondaryadapter.mdc;

public final class TrazaKeys {

    public static final String CORRELACION_ID = "correlacionId";
    public static final String TRANSACCION_ID = "transaccionId";
    public static final String USUARIO_ID = "usuarioId";
    public static final String ORIGEN = "origen";
    public static final String CLIENTE_IP = "clienteIp";
    public static final String METODO_HTTP = "metodoHttp";
    public static final String RUTA_URI = "rutaUri";
    public static final String COLA_EVENTO = "colaEvento";
    public static final String TIEMPO_ENTRADA = "tiempoEntrada";

    public static final String TIEMPO_SALIDA = "tiempoSalida";
    public static final String DURACION_MS = "duracionMs";
    public static final String CODIGO_ESTADO = "codigoEstado";

    private TrazaKeys() {}
}
