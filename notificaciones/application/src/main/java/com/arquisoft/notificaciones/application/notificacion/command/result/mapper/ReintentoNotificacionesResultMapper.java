package com.arquisoft.notificaciones.application.notificacion.command.result.mapper;

import com.arquisoft.notificaciones.application.notificacion.command.result.ReintentoNotificacionesResult;

public final class ReintentoNotificacionesResultMapper {

    private ReintentoNotificacionesResultMapper() {}

    public static ReintentoNotificacionesResult toResult(
            int reenviadas, int fallidas, int agotadas) {
        return new ReintentoNotificacionesResult(reenviadas, fallidas, agotadas);
    }
}
