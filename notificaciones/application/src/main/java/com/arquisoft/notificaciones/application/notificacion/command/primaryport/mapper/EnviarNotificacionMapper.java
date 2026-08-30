package com.arquisoft.notificaciones.application.notificacion.command.primaryport.mapper;

import com.arquisoft.notificaciones.application.notificacion.command.primaryport.model.EnviarNotificacionCommand;
import com.arquisoft.notificaciones.domain.notificacion.NotificacionDomain;

public final class EnviarNotificacionMapper {

    private EnviarNotificacionMapper() {}

    public static NotificacionDomain toDomain(EnviarNotificacionCommand command) {
        return NotificacionDomain.crear(
                command.idEvento(),
                command.tipo(),
                command.destinatarioEmail(),
                command.asunto(),
                command.destinatarioNombre(),
                command.cuerpo());
    }
}
