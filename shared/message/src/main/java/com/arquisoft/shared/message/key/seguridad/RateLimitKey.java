package com.arquisoft.shared.message.key.seguridad;

import com.arquisoft.shared.message.MessageBundles;
import com.arquisoft.shared.message.MessageKey;

/** Claves de RateLimit. */
public enum RateLimitKey implements MessageKey {

    ERROR_LIMITE_EXCEDIDO("seguridad.infraestructura.ratelimit.error.limite-excedido"),
    ERROR_HTTP_TOO_MANY_REQUESTS("seguridad.infraestructura.ratelimit.error.http-too-many-requests"),
    ERROR_CLIENTE_STANDALONE("seguridad.infraestructura.ratelimit.error.cliente-standalone"),
    LOG_LIMITE_EXCEDIDO("seguridad.infraestructura.ratelimit.log.limite-excedido"),
    LOG_INIT_OK("seguridad.infraestructura.ratelimit.log.init-ok"),
    LOG_CLIENTE_STANDALONE_ERROR("seguridad.infraestructura.ratelimit.log.cliente-standalone-error"),
    LOG_BUCKET_REDIS_ERROR("seguridad.infraestructura.ratelimit.log.bucket-redis-error"),
    LOG_BUCKET_LOGIN_REDIS_ERROR("seguridad.infraestructura.ratelimit.log.bucket-login-redis-error");

    private final String clave;

    RateLimitKey(String clave) {
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
