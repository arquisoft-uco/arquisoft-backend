package com.arquisoft.notificaciones.infrastructure.notificacion.persistence;

import com.arquisoft.notificaciones.domain.notificacion.aggregate.NotificacionDomain;
import com.arquisoft.notificaciones.domain.notificacion.model.EstadoNotificacion;
import com.arquisoft.notificaciones.domain.notificacion.model.TipoNotificacion;

public final class NotificacionMapper {

    private NotificacionMapper() {}

    public static NotificacionEntity toEntity(NotificacionDomain aggregate) {
        return NotificacionEntity.builder()
                .id(aggregate.getId())
                .idEvento(aggregate.getIdEvento())
                .tipo(aggregate.getTipo().getCodigo())
                .destinatario(aggregate.getDestinatario())
                .asunto(aggregate.getAsunto())
                .estado(aggregate.getEstado().name())
                .detalleError(aggregate.getDetalleError())
                .fechaCreacion(aggregate.getFechaCreacion())
                .fechaEnvio(aggregate.getFechaEnvio())
                .build();
    }

    public static NotificacionDomain toDomain(NotificacionEntity entity) {
        return NotificacionDomain.reconstruir(
                new NotificacionDomain.DatosNotificacion(
                        entity.getId(),
                        entity.getIdEvento(),
                        TipoNotificacion.valueOf(entity.getTipo()),
                        entity.getDestinatario(),
                        entity.getAsunto(),
                        entity.getFechaCreacion(),
                        entity.getFechaEnvio()),
                EstadoNotificacion.valueOf(entity.getEstado()),
                entity.getDetalleError());
    }
}
