package com.arquisoft.evaluaciones.infrastructure.observacionitemasesor.query.primaryadapter.web.mapper;

import com.arquisoft.evaluaciones.application.observacionitemasesor.query.readmodel.ObservacionItemAsesorReadModel;
import com.arquisoft.evaluaciones.infrastructure.observacionitemasesor.query.primaryadapter.web.dto.ObservacionItemAsesorResponseDTO;

public final class ObservacionItemAsesorResponseMapper {

    private ObservacionItemAsesorResponseMapper() {}

    public static ObservacionItemAsesorResponseDTO toResponse(ObservacionItemAsesorReadModel readModel) {
        return new ObservacionItemAsesorResponseDTO(readModel.id(), readModel.descripcion());
    }
}
