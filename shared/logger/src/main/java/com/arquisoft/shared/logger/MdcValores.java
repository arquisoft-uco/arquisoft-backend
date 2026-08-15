package com.arquisoft.shared.logger;

/**
 * Valores centinela de los campos de MDC cuando no hay un usuario real detrás de la operación.
 *
 * <p>Van aquí y no al catálogo de mensajes por la misma razón que {@link MdcKeys}: no son texto
 * para leer sino el valor exacto contra el que se filtra en Loki. Traducirlos o reformularlos
 * rompería consultas y alertas ya escritas.
 */
public final class MdcValores {

    private MdcValores() {}

    /** Petición HTTP sin autenticar. */
    public static final String ANONIMO = "ANONYMOUS";

    /** Mensaje publicado por el propio backend, no por una petición de usuario. */
    public static final String SISTEMA = "SYSTEM";

    /** Procesamiento de un evento consumido de la cola. */
    public static final String EVENTO = "EVENT";
}
