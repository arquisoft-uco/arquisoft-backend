package com.arquisoft.fichas.infrastructure.observacionitem.query.primaryadapter.web.mapper;

import com.arquisoft.fichas.application.observacionitem.query.readmodel.ObservacionItemReadModel;
import com.arquisoft.fichas.infrastructure.observacionitem.query.primaryadapter.web.dto.ObservacionItemResponseDTO;

public final class ObservacionItemResponseMapper {

    private ObservacionItemResponseMapper() {}

    public static ObservacionItemResponseDTO toResponse(ObservacionItemReadModel readModel) {
        return new ObservacionItemResponseDTO(
                readModel.id(),
                readModel.revisionItem(),
                readModel.observacion(),
                readModel.estadoObservacionRevision(),
                readModel.estadoObservacionRevisionNombre());
    }
}
