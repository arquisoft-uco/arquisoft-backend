package com.arquisoft.notificaciones.infrastructure.notificacion.command.secondaryadapter.mapper;

import com.arquisoft.notificaciones.application.notificacion.command.secondaryport.entity.NotificacionEntity;
import com.arquisoft.notificaciones.infrastructure.notificacion.command.secondaryadapter.entity.NotificacionJpaEntity;

public final class NotificacionJpaMapper {

    private NotificacionJpaMapper() {}

    public static NotificacionEntity toEntity(NotificacionJpaEntity jpaEntity) {
        return new NotificacionEntity(
                jpaEntity.getId(),
                jpaEntity.getIdEvento(),
                jpaEntity.getTipo(),
                jpaEntity.getDestinatario(),
                jpaEntity.getAsunto(),
                jpaEntity.getEstado(),
                jpaEntity.getDetalleError(),
                jpaEntity.getFechaCreacion(),
                jpaEntity.getFechaEnvio());
    }

    public static NotificacionJpaEntity toJpaEntity(NotificacionEntity entity) {
        return NotificacionJpaEntity.builder()
                .id(entity.id())
                .idEvento(entity.idEvento())
                .tipo(entity.tipo())
                .destinatario(entity.destinatario())
                .asunto(entity.asunto())
                .estado(entity.estado())
                .detalleError(entity.detalleError())
                .fechaCreacion(entity.fechaCreacion())
                .fechaEnvio(entity.fechaEnvio())
                .build();
    }
}
