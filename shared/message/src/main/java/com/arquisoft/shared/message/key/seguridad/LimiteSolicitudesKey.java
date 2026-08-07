package com.arquisoft.shared.message.key.seguridad;

import com.arquisoft.shared.message.PaquetesMensajes;
import com.arquisoft.shared.message.ClaveMensaje;

/** Claves de RateLimit. */
public enum LimiteSolicitudesKey implements ClaveMensaje {

    ERROR_LIMITE_EXCEDIDO("seguridad.infraestructura.ratelimit.error.limite-excedido"),
    ERROR_HTTP_DEMASIADAS_SOLICITUDES("seguridad.infraestructura.ratelimit.error.http-demasiadas-solicitudes"),
    ERROR_CLIENTE_STANDALONE("seguridad.infraestructura.ratelimit.error.cliente-standalone"),
    LOG_LIMITE_EXCEDIDO("seguridad.infraestructura.ratelimit.log.limite-excedido"),
    LOG_INIT_OK("seguridad.infraestructura.ratelimit.log.init-ok"),
    LOG_CLIENTE_STANDALONE_ERROR("seguridad.infraestructura.ratelimit.log.cliente-standalone-error"),
    LOG_BUCKET_REDIS_ERROR("seguridad.infraestructura.ratelimit.log.bucket-redis-error"),
    LOG_BUCKET_LOGIN_REDIS_ERROR("seguridad.infraestructura.ratelimit.log.bucket-login-redis-error");

    private final String clave;

    LimiteSolicitudesKey(String clave) {
        this.clave = clave;
    }

    @Override
    public String clave() {
        return clave;
    }

    @Override
    public String paquete() {
        return PaquetesMensajes.SEGURIDAD;
    }
}
