package com.arquisoft.notificaciones.infrastructure.notificacion.persistence;

import com.arquisoft.notificaciones.domain.notificacion.aggregate.NotificacionAggregate;
import com.arquisoft.notificaciones.domain.notificacion.model.EstadoNotificacion;
import com.arquisoft.notificaciones.domain.notificacion.model.TipoNotificacion;

public final class NotificacionMapper {

    private NotificacionMapper() {}

    public static NotificacionEntity toEntity(NotificacionAggregate aggregate) {
        return NotificacionEntity.builder()
                .id(aggregate.getId())
                .eventId(aggregate.getEventId())
                .tipo(aggregate.getTipo().getCodigo())
                .destinatario(aggregate.getDestinatario())
                .asunto(aggregate.getAsunto())
                .estado(aggregate.getEstado().name())
                .detalleError(aggregate.getDetalleError())
                .fechaCreacion(aggregate.getFechaCreacion())
                .fechaEnvio(aggregate.getFechaEnvio())
                .build();
    }

    public static NotificacionAggregate toDomain(NotificacionEntity entity) {
        return NotificacionAggregate.reconstruir(
                new NotificacionAggregate.DatosNotificacion(
                        entity.getId(),
                        entity.getEventId(),
                        TipoNotificacion.valueOf(entity.getTipo()),
                        entity.getDestinatario(),
                        entity.getAsunto(),
                        entity.getFechaCreacion(),
                        entity.getFechaEnvio()),
                EstadoNotificacion.valueOf(entity.getEstado()),
                entity.getDetalleError());
    }
}
