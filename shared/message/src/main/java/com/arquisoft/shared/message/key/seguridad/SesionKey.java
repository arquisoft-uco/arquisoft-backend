package com.arquisoft.shared.message.key.seguridad;

import com.arquisoft.shared.message.ClaveMensaje;

/** Claves de Sesion. */
public enum SesionKey implements ClaveMensaje {

    ERROR_IDENTIFICADOR_REQUERIDO("seguridad.dominio.sesion.error.identificador-requerido", 0),
    ERROR_TTL_INVALIDO("seguridad.dominio.sesion.error.ttl-invalido", 0),
    CUERPO_SESION_CERRADA("seguridad.infraestructura.sesion.cuerpo.cerrada", 0),
    LOG_LOGOUT_EXITOSO("seguridad.aplicacion.sesion.log.logout-exitoso", 0),
    LOG_LOGOUT_TOKEN_EXPIRADO("seguridad.aplicacion.sesion.log.logout-token-expirado", 0);

    private final String clave;
    private final int parametros;

    SesionKey(String clave, int parametros) {
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
