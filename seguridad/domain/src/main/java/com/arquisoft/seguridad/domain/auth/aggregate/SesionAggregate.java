package com.arquisoft.seguridad.domain.auth.aggregate;

import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.SeguridadMessages;

public final class SesionAggregate {

    private final String identificadorToken;
    private final long tiempoVidaRestante;

    private SesionAggregate(String identificadorToken, long tiempoVidaRestante) {
        this.identificadorToken = identificadorToken;
        this.tiempoVidaRestante = tiempoVidaRestante;
    }

    public static SesionAggregate cerrar(String identificadorToken, long tiempoVidaRestante) {
        if (identificadorToken == null || identificadorToken.isBlank()) {
            throw new DomainException(SeguridadMessages.Sesion.IDENTIFICADOR_REQUERIDO,
                    SeguridadMessages.Sesion.SESION_IDENTIFICADOR_REQUERIDO);
        }
        if (tiempoVidaRestante <= 0) {
            throw new DomainException(SeguridadMessages.Sesion.TTL_INVALIDO,
                    SeguridadMessages.Sesion.SESION_TTL_INVALIDO);
        }
        return new SesionAggregate(identificadorToken, tiempoVidaRestante);
    }

    public String identificadorToken() {
        return identificadorToken;
    }

    public long tiempoVidaRestante() {
        return tiempoVidaRestante;
    }
}
