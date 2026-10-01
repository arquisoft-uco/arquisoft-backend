package com.arquisoft.solicitudes.infrastructure.respuesta.command.secondaryadapter.mapper;

import com.arquisoft.solicitudes.application.respuesta.command.secondaryport.entity.RespuestaEntity;
import com.arquisoft.solicitudes.infrastructure.respuesta.command.secondaryadapter.entity.RespuestaJpaEntity;

public final class RespuestaJpaMapper {

    private RespuestaJpaMapper() {}

    public static RespuestaEntity toEntity(RespuestaJpaEntity jpaEntity) {
        return new RespuestaEntity(
                jpaEntity.getId(),
                jpaEntity.getSolicitudId(),
                jpaEntity.getFechaRespuesta(),
                jpaEntity.getContenido(),
                jpaEntity.getEstadoRespuesta().getId());
    }

    public static RespuestaJpaEntity toJpaEntity(RespuestaEntity entity) {
        return RespuestaJpaEntity.builder()
                .id(entity.id())
                .solicitudId(entity.solicitud())
                .fechaRespuesta(entity.fechaRespuesta())
                .contenido(entity.contenido())
                .estadoRespuesta(EstadoRespuestaJpaMapper.toReferencia(entity.estadoRespuesta()))
                .build();
    }
}
