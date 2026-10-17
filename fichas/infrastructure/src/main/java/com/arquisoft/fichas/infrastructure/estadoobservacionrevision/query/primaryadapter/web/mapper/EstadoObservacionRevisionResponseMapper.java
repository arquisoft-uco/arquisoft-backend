package com.arquisoft.fichas.infrastructure.estadoobservacionrevision.query.primaryadapter.web.mapper;

import com.arquisoft.fichas.application.estadoobservacionrevision.query.readmodel.EstadoObservacionRevisionReadModel;
import com.arquisoft.fichas.infrastructure.estadoobservacionrevision.query.primaryadapter.web.dto.EstadoObservacionRevisionResponseDTO;

public final class EstadoObservacionRevisionResponseMapper {

    private EstadoObservacionRevisionResponseMapper() {}

    public static EstadoObservacionRevisionResponseDTO toResponse(EstadoObservacionRevisionReadModel readModel) {
        return new EstadoObservacionRevisionResponseDTO(
                readModel.id(),
                readModel.nombre(),
                readModel.descripcion());
    }
}
