package com.arquisoft.shared.message.key.seguridad;

import com.arquisoft.shared.message.ClaveMensaje;

/** Claves de JwtBlacklist. */
public enum TokenInvalidadoKey implements ClaveMensaje {

    ERROR_HTTP_401("seguridad.infraestructura.jwtblacklist.error.http-401", 0),
    ERROR_HTTP_401_DETALLE("seguridad.infraestructura.jwtblacklist.error.http-401-detalle", 0),
    LOG_TOKEN_REVOCADO("seguridad.infraestructura.jwtblacklist.log.token-revocado", 2),
    LOG_REDIS_NO_DISPONIBLE("seguridad.infraestructura.jwtblacklist.log.redis-no-disponible", 1);

    private final String clave;
    private final int parametros;

    TokenInvalidadoKey(String clave, int parametros) {
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
