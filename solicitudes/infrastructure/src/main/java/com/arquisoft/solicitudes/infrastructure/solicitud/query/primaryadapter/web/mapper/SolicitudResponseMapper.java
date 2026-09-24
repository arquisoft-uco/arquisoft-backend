package com.arquisoft.solicitudes.infrastructure.solicitud.query.primaryadapter.web.mapper;

import com.arquisoft.solicitudes.application.solicitud.query.readmodel.SolicitudReadModel;
import com.arquisoft.solicitudes.infrastructure.remitente.query.primaryadapter.web.dto.RemitenteResponseDTO;
import com.arquisoft.solicitudes.infrastructure.solicitud.query.primaryadapter.web.dto.SolicitudResponseDTO;

public final class SolicitudResponseMapper {

    private SolicitudResponseMapper() {}

    public static SolicitudResponseDTO toResponse(SolicitudReadModel readModel) {
        return new SolicitudResponseDTO(
                readModel.id(),
                readModel.mensajeSolicitud(),
                readModel.fechaCreacion(),
                readModel.tipoSolicitudId(),
                readModel.tipoSolicitudNombre(),
                new RemitenteResponseDTO(
                        readModel.remitente().usuarioId(),
                        readModel.remitente().identificador(),
                        readModel.remitente().nombre(),
                        readModel.remitente().email()));
    }
}
