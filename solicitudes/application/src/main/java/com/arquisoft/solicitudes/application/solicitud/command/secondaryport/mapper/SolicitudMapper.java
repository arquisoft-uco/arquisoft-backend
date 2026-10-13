package com.arquisoft.solicitudes.application.solicitud.command.secondaryport.mapper;

import com.arquisoft.solicitudes.application.solicitud.command.secondaryport.entity.SolicitudEntity;
import com.arquisoft.solicitudes.domain.solicitud.SolicitudDomain;

import java.util.UUID;

public final class SolicitudMapper {

    private SolicitudMapper() {}

    public static SolicitudEntity toEntity(SolicitudDomain domain, UUID remitente, UUID destinatario) {
        return new SolicitudEntity(
                domain.getId(),
                destinatario,
                remitente,
                domain.getFechaCreacion(),
                domain.getMensajeSolicitud(),
                domain.getTipoSolicitud().getId());
    }
}
