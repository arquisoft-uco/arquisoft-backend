package com.arquisoft.shared.message.key.app;

import com.arquisoft.shared.message.PaquetesMensajes;
import com.arquisoft.shared.message.ClaveMensaje;

/** Mensajes que produce {@code DomainValidator} al acumular errores de integridad. */
public enum ValidadorKey implements ClaveMensaje {

    NO_NULO("app.dominio.validador.error.no-nulo"),
    NO_EN_BLANCO("app.dominio.validador.error.no-en-blanco"),
    LONGITUD_MAXIMA("app.dominio.validador.error.longitud-maxima"),
    LONGITUD_MINIMA("app.dominio.validador.error.longitud-minima"),
    CORREO_INVALIDO("app.dominio.validador.error.correo-invalido"),
    UUID_INVALIDO("app.dominio.validador.error.uuid-invalido"),
    COLECCION_VACIA("app.dominio.validador.error.coleccion-vacia"),
    TAMANIO_MAXIMO("app.dominio.validador.error.tamanio-maximo"),
    SIN_DUPLICADOS("app.dominio.validador.error.sin-duplicados");

    private final String clave;

    ValidadorKey(String clave) {
        this.clave = clave;
    }

    @Override
    public String clave() {
        return clave;
    }

    @Override
    public String paquete() {
        return PaquetesMensajes.APP;
    }
}
