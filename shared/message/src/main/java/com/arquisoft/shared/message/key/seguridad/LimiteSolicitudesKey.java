package com.arquisoft.shared.message.key.seguridad;

import com.arquisoft.shared.message.ClaveMensaje;

/** Claves de RateLimit. */
public enum LimiteSolicitudesKey implements ClaveMensaje {

    ERROR_LIMITE_EXCEDIDO("seguridad.infraestructura.ratelimit.error.limite-excedido", 1),
    ERROR_HTTP_DEMASIADAS_SOLICITUDES("seguridad.infraestructura.ratelimit.error.http-demasiadas-solicitudes", 0),
    ERROR_CLIENTE_STANDALONE("seguridad.infraestructura.ratelimit.error.cliente-standalone", 0),
    LOG_LIMITE_EXCEDIDO("seguridad.infraestructura.ratelimit.log.limite-excedido", 2),
    LOG_INIT_OK("seguridad.infraestructura.ratelimit.log.init-ok", 0),
    LOG_CLIENTE_STANDALONE_ERROR("seguridad.infraestructura.ratelimit.log.cliente-standalone-error", 1),
    LOG_DEGRADADO("seguridad.infraestructura.ratelimit.log.degradado", 1),
    LOG_SIGUE_DEGRADADO("seguridad.infraestructura.ratelimit.log.sigue-degradado", 0),
    LOG_RECUPERADO("seguridad.infraestructura.ratelimit.log.recuperado", 1);

    private final String clave;
    private final int parametros;

    LimiteSolicitudesKey(String clave, int parametros) {
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
