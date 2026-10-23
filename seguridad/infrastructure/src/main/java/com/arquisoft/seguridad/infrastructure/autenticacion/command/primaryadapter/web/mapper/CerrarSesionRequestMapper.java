package com.arquisoft.seguridad.infrastructure.autenticacion.command.primaryadapter.web.mapper;

import com.arquisoft.seguridad.application.autenticacion.command.primaryport.model.TokenSesionCommand;
import com.arquisoft.shared.util.UtilFecha;
import com.arquisoft.shared.util.UtilObjeto;
import com.arquisoft.shared.web.identidad.IdentidadActual;

import java.time.Duration;

public final class CerrarSesionRequestMapper {

    private static final long TTL_MINIMO = 1L;
    private static final long TTL_EXPIRADO = 0L;

    private CerrarSesionRequestMapper() {}

    public static TokenSesionCommand toCommand(IdentidadActual identidad) {
        return new TokenSesionCommand(identidad.idToken(), calcularTiempoVidaRestante(identidad));
    }

    private static long calcularTiempoVidaRestante(IdentidadActual identidad) {
        var expiracion = identidad.expiraEn();
        var ahora = UtilFecha.generarInstanteActual();
        if (UtilObjeto.esNulo(expiracion) || !ahora.isBefore(expiracion)) {
            return TTL_EXPIRADO;
        }
        return Math.max(TTL_MINIMO, Duration.between(ahora, expiracion).toSeconds());
    }
}
