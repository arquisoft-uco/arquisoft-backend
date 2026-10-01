package com.arquisoft.seguridad.infrastructure.auth.command.primaryadapter.web.mapper;

import com.arquisoft.seguridad.application.auth.command.primaryport.model.TokenSesionCommand;
import com.arquisoft.shared.util.UtilFecha;
import com.arquisoft.shared.util.UtilObjeto;
import org.springframework.security.oauth2.jwt.Jwt;

import java.time.Duration;

public final class CerrarSesionRequestMapper {

    private static final long TTL_MINIMO = 1L;
    private static final long TTL_EXPIRADO = 0L;

    private CerrarSesionRequestMapper() {}

    public static TokenSesionCommand toCommand(Jwt jwt) {
        return new TokenSesionCommand(jwt.getId(), calcularTiempoVidaRestante(jwt));
    }

    private static long calcularTiempoVidaRestante(Jwt jwt) {
        var expiracion = jwt.getExpiresAt();
        var ahora = UtilFecha.generarInstanteActual();
        if (UtilObjeto.esNulo(expiracion) || !ahora.isBefore(expiracion)) {
            return TTL_EXPIRADO;
        }
        return Math.max(TTL_MINIMO, Duration.between(ahora, expiracion).toSeconds());
    }
}
