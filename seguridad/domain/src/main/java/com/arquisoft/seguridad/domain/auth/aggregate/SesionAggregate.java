package com.arquisoft.seguridad.domain.auth.aggregate;

import com.arquisoft.shared.message.Messages;
import com.arquisoft.shared.message.SeguridadCodes;
import com.arquisoft.shared.message.SeguridadKeys;
import com.arquisoft.shared.exception.DomainException;

public final class SesionAggregate {

    private final String identificadorToken;
    private final long tiempoVidaRestante;

    private SesionAggregate(String identificadorToken, long tiempoVidaRestante) {
        this.identificadorToken = identificadorToken;
        this.tiempoVidaRestante = tiempoVidaRestante;
    }

    public static SesionAggregate cerrar(String identificadorToken, long tiempoVidaRestante) {
        if (identificadorToken == null || identificadorToken.isBlank()) {
            throw new DomainException(Messages.obtener(SeguridadKeys.Sesion.ERROR_IDENTIFICADOR_REQUERIDO),
                    SeguridadCodes.Sesion.SESION_IDENTIFICADOR_REQUERIDO);
        }
        if (tiempoVidaRestante <= 0) {
            throw new DomainException(Messages.obtener(SeguridadKeys.Sesion.ERROR_TTL_INVALIDO),
                    SeguridadCodes.Sesion.SESION_TTL_INVALIDO);
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
