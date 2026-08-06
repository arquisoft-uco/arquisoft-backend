package com.arquisoft.shared.message.key.seguridad;

import com.arquisoft.shared.message.MessageBundles;
import com.arquisoft.shared.message.MessageKey;

/** Claves de JwtBlacklist. */
public enum JwtBlacklistKey implements MessageKey {

    ERROR_HTTP_401("seguridad.infraestructura.jwtblacklist.error.http-401"),
    ERROR_HTTP_401_DETALLE("seguridad.infraestructura.jwtblacklist.error.http-401-detalle"),
    ERROR_HTTP_503("seguridad.infraestructura.jwtblacklist.error.http-503"),
    ERROR_HTTP_503_DETALLE("seguridad.infraestructura.jwtblacklist.error.http-503-detalle"),
    LOG_TOKEN_REVOCADO("seguridad.infraestructura.jwtblacklist.log.token-revocado"),
    LOG_REDIS_NO_DISPONIBLE("seguridad.infraestructura.jwtblacklist.log.redis-no-disponible");

    private final String clave;

    JwtBlacklistKey(String clave) {
        this.clave = clave;
    }

    @Override
    public String clave() {
        return clave;
    }

    @Override
    public String bundle() {
        return MessageBundles.SEGURIDAD;
    }
}
