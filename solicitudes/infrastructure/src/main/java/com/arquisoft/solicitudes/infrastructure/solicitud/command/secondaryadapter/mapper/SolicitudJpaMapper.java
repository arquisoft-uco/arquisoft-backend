package com.arquisoft.solicitudes.infrastructure.solicitud.command.secondaryadapter.mapper;

import com.arquisoft.solicitudes.application.solicitud.command.secondaryport.entity.SolicitudEntity;
import com.arquisoft.solicitudes.infrastructure.destinatario.command.secondaryadapter.mapper.DestinatarioJpaMapper;
import com.arquisoft.solicitudes.infrastructure.remitente.command.secondaryadapter.mapper.RemitenteJpaMapper;
import com.arquisoft.solicitudes.infrastructure.solicitud.command.secondaryadapter.entity.SolicitudJpaEntity;
import com.arquisoft.solicitudes.infrastructure.tiposolicitud.command.secondaryadapter.mapper.TipoSolicitudJpaMapper;

public final class SolicitudJpaMapper {

    private SolicitudJpaMapper() {}

    public static SolicitudEntity toEntity(SolicitudJpaEntity jpaEntity) {
        return new SolicitudEntity(
                jpaEntity.getId(),
                jpaEntity.getDestinatario().getId(),
                jpaEntity.getRemitente().getId(),
                jpaEntity.getFechaCreacion(),
                jpaEntity.getMensajeSolicitud(),
                jpaEntity.getTipoSolicitud().getId());
    }

    public static SolicitudJpaEntity toJpaEntity(SolicitudEntity entity) {
        return SolicitudJpaEntity.builder()
                .id(entity.id())
                .destinatario(DestinatarioJpaMapper.toReferencia(entity.destinatario()))
                .remitente(RemitenteJpaMapper.toReferencia(entity.remitente()))
                .fechaCreacion(entity.fechaCreacion())
                .mensajeSolicitud(entity.mensajeSolicitud())
                .tipoSolicitud(TipoSolicitudJpaMapper.toReferencia(entity.tipoSolicitud()))
                .build();
    }
}
