package com.arquisoft.shared.message.key.app;

import com.arquisoft.shared.message.ClaveMensaje;

/** Mensajes que producen los {@code Validator*} al acumular errores de integridad. */
public enum ValidadorKey implements ClaveMensaje {

    NO_NULO("app.dominio.validador.error.no-nulo", 1),
    NO_EN_BLANCO("app.dominio.validador.error.no-en-blanco", 1),
    LONGITUD_MAXIMA("app.dominio.validador.error.longitud-maxima", 2),
    LONGITUD_MINIMA("app.dominio.validador.error.longitud-minima", 2),
    LONGITUD_ENTRE("app.dominio.validador.error.longitud-entre", 3),
    VALOR_MINIMO("app.dominio.validador.error.valor-minimo", 2),
    VALOR_MAXIMO("app.dominio.validador.error.valor-maximo", 2),
    VALOR_ENTRE("app.dominio.validador.error.valor-entre", 3),
    CORREO_INVALIDO("app.dominio.validador.error.correo-invalido", 1),
    UUID_INVALIDO("app.dominio.validador.error.uuid-invalido", 1),
    COLECCION_VACIA("app.dominio.validador.error.coleccion-vacia", 1),
    TAMANIO_MAXIMO("app.dominio.validador.error.tamanio-maximo", 2),
    SIN_DUPLICADOS("app.dominio.validador.error.sin-duplicados", 2);

    private final String clave;
    private final int parametros;

    ValidadorKey(String clave, int parametros) {
        this.clave = clave;
        this.parametros = parametros;
    }

    @Override
    public String clave() {
        return clave;
    }

    @Override
    public int parametros() {
        return parametros;
    }
}
