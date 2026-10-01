package com.arquisoft.solicitudes.infrastructure.tiposolicitud.query.primaryadapter.web.mapper;

import com.arquisoft.solicitudes.application.tiposolicitud.query.readmodel.TipoSolicitudReadModel;
import com.arquisoft.solicitudes.infrastructure.tiposolicitud.query.primaryadapter.web.dto.TipoSolicitudResponseDTO;

public final class TipoSolicitudResponseMapper {

    private TipoSolicitudResponseMapper() {}

    public static TipoSolicitudResponseDTO toResponse(TipoSolicitudReadModel readModel) {
        return new TipoSolicitudResponseDTO(
                readModel.id(),
                readModel.nombre(),
                readModel.descripcion());
    }
}
