package com.arquisoft.notificaciones.application.notificacion.command.primaryport.mapper;

import com.arquisoft.notificaciones.application.notificacion.command.primaryport.model.ReintentarNotificacionesFallidasCommand;
import com.arquisoft.notificaciones.domain.notificacion.ReintentoNotificacionesDomain;

public final class ReintentarNotificacionesFallidasMapper {

    private ReintentarNotificacionesFallidasMapper() {}

    public static ReintentoNotificacionesDomain toDomain(
            ReintentarNotificacionesFallidasCommand command) {
        return ReintentoNotificacionesDomain.crear(command.maxIntentos(), command.limite());
    }
}
