package com.arquisoft.seguridad.domain.auth.aggregate;

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
            throw new DomainException("El identificador del token no puede ser nulo ni vacio",
                    "SESION_IDENTIFICADOR_REQUERIDO");
        }
        if (tiempoVidaRestante <= 0) {
            throw new DomainException("El tiempo de vida restante debe ser mayor a cero",
                    "SESION_TTL_INVALIDO");
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
