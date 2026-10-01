package com.arquisoft.solicitudes.application.solicitud.command.secondaryport.mapper;

import com.arquisoft.solicitudes.application.solicitud.command.secondaryport.entity.SolicitudEntity;
import com.arquisoft.solicitudes.domain.solicitud.SolicitudDomain;
import com.arquisoft.solicitudes.domain.tiposolicitud.TipoSolicitud;

public final class SolicitudMapper {

    private SolicitudMapper() {}

    public static SolicitudEntity toEntity(SolicitudDomain domain) {
        return new SolicitudEntity(
                domain.getId(),
                domain.getDestinatario(),
                domain.getRemitente(),
                domain.getFechaCreacion(),
                domain.getMensajeSolicitud(),
                domain.getTipoSolicitud().getId());
    }

    public static SolicitudDomain toDomain(SolicitudEntity entity) {
        return SolicitudDomain.reconstruir(
                entity.id(),
                entity.destinatario(),
                entity.remitente(),
                entity.fechaCreacion(),
                entity.mensajeSolicitud(),
                TipoSolicitud.desde(entity.tipoSolicitud()));
    }
}
