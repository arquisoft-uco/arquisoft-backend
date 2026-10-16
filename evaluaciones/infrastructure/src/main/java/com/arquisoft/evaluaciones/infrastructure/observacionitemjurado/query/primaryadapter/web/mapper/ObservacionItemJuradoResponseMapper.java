package com.arquisoft.evaluaciones.infrastructure.observacionitemjurado.query.primaryadapter.web.mapper;

import com.arquisoft.evaluaciones.application.observacionitemjurado.query.readmodel.ObservacionItemJuradoReadModel;
import com.arquisoft.evaluaciones.infrastructure.observacionitemjurado.query.primaryadapter.web.dto.ObservacionItemJuradoResponseDTO;

public final class ObservacionItemJuradoResponseMapper {

    private ObservacionItemJuradoResponseMapper() {}

    public static ObservacionItemJuradoResponseDTO toResponse(ObservacionItemJuradoReadModel readModel) {
        return new ObservacionItemJuradoResponseDTO(readModel.id(), readModel.descripcion());
    }
}
