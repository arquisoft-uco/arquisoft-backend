package com.arquisoft.shared.redis.catalogo.messageuco;

/**
 * Lo que dejó un aviso de cambio de un solo mensaje.
 */
public enum ResultadoActualizacion {

    /** El texto cambió y se escribió en Redis. */
    ACTUALIZADA,

    /** Redis ya tenía ese texto. */
    SIN_CAMBIOS,

    /** El título del mensaje no es una clave declarada del catálogo; no se escribe. */
    NO_ES_CLAVE,

    /** Texto vacío o con aridad distinta a la declarada; Redis conserva el anterior. */
    RECHAZADA,

    /** MessageUcoLab respondió sin mensaje; no hay nada que escribir. */
    NO_ENCONTRADO
}
