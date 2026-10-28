package com.arquisoft.shared.message.key.app;

import com.arquisoft.shared.message.ClaveMensaje;

/** Mensajes que producen los {@code Validator*} al acumular errores de integridad. */
public enum ValidadorKey implements ClaveMensaje {

    NO_NULO("app.dominio.validador.error.no-nulo", 0),
    NO_EN_BLANCO("app.dominio.validador.error.no-en-blanco", 0),
    LONGITUD_MAXIMA("app.dominio.validador.error.longitud-maxima", 1),
    LONGITUD_MINIMA("app.dominio.validador.error.longitud-minima", 1),
    LONGITUD_ENTRE("app.dominio.validador.error.longitud-entre", 2),
    VALOR_MINIMO("app.dominio.validador.error.valor-minimo", 1),
    VALOR_MAXIMO("app.dominio.validador.error.valor-maximo", 1),
    VALOR_ENTRE("app.dominio.validador.error.valor-entre", 2),
    CORREO_INVALIDO("app.dominio.validador.error.correo-invalido", 0),
    UUID_INVALIDO("app.dominio.validador.error.uuid-invalido", 0),
    COLECCION_VACIA("app.dominio.validador.error.coleccion-vacia", 0),
    TAMANIO_MAXIMO("app.dominio.validador.error.tamanio-maximo", 1),
    SIN_DUPLICADOS("app.dominio.validador.error.sin-duplicados", 0),
    FECHA_INVALIDA("app.dominio.validador.error.fecha-invalida", 0),
    FECHA_POSTERIOR("app.dominio.validador.error.fecha-posterior", 1);

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
