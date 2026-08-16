package com.arquisoft.notificaciones.application.notificacion.command.secondaryport.mapper;

import com.arquisoft.notificaciones.application.notificacion.command.secondaryport.entity.NotificacionEntity;
import com.arquisoft.notificaciones.domain.notificacion.NotificacionDomain;
import com.arquisoft.notificaciones.domain.notificacion.model.EstadoNotificacion;
import com.arquisoft.notificaciones.domain.notificacion.model.TipoNotificacion;

public final class NotificacionMapper {

    private NotificacionMapper() {}

    public static NotificacionEntity toEntity(NotificacionDomain aggregate) {
        return new NotificacionEntity(
                aggregate.getId(),
                aggregate.getIdEvento(),
                aggregate.getTipo().getId(),
                aggregate.getDestinatario(),
                aggregate.getAsunto(),
                aggregate.getEstado().getId(),
                aggregate.getDetalleError(),
                aggregate.getFechaCreacion(),
                aggregate.getFechaEnvio());
    }

    public static NotificacionDomain toDomain(NotificacionEntity entity) {
        return NotificacionDomain.reconstruir(
                new NotificacionDomain.DatosNotificacion(
                        entity.id(),
                        entity.idEvento(),
                        TipoNotificacion.desde(entity.tipo()),
                        entity.destinatario(),
                        entity.asunto(),
                        entity.fechaCreacion(),
                        entity.fechaEnvio()),
                EstadoNotificacion.desde(entity.estado()),
                entity.detalleError());
    }
}
