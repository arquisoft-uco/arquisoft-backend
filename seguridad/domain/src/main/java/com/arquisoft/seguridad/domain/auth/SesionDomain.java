package com.arquisoft.seguridad.domain.auth;

import com.arquisoft.shared.message.key.seguridad.SesionKey;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.SeguridadCodes;
import com.arquisoft.shared.exception.DomainException;

public final class SesionDomain {

    private final String identificadorToken;
    private final long tiempoVidaRestante;

    private SesionDomain(String identificadorToken, long tiempoVidaRestante) {
        this.identificadorToken = identificadorToken;
        this.tiempoVidaRestante = tiempoVidaRestante;
    }

    public static SesionDomain cerrar(String identificadorToken, long tiempoVidaRestante) {
        if (identificadorToken == null || identificadorToken.isBlank()) {
            throw new DomainException(Mensajes.obtener(SesionKey.ERROR_IDENTIFICADOR_REQUERIDO),
                    SeguridadCodes.Sesion.SESION_IDENTIFICADOR_REQUERIDO);
        }
        if (tiempoVidaRestante <= 0) {
            throw new DomainException(Mensajes.obtener(SesionKey.ERROR_TTL_INVALIDO),
                    SeguridadCodes.Sesion.SESION_TTL_INVALIDO);
        }
        return new SesionDomain(identificadorToken, tiempoVidaRestante);
    }

    public String identificadorToken() {
        return identificadorToken;
    }

    public long tiempoVidaRestante() {
        return tiempoVidaRestante;
    }
}
