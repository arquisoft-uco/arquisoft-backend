package com.arquisoft.evaluaciones.infrastructure.evaluacionjurado.query.primaryadapter.web.mapper;

import com.arquisoft.evaluaciones.application.evaluacionjurado.query.readmodel.EvaluacionJuradoReadModel;
import com.arquisoft.evaluaciones.infrastructure.evaluacionjurado.query.primaryadapter.web.dto.EvaluacionJuradoResponseDTO;

public final class EvaluacionJuradoResponseMapper {

    private EvaluacionJuradoResponseMapper() {}

    public static EvaluacionJuradoResponseDTO toResponse(EvaluacionJuradoReadModel readModel) {
        var jurado = readModel.jurado();
        return new EvaluacionJuradoResponseDTO(
                readModel.id(),
                new EvaluacionJuradoResponseDTO.JuradoDTO(jurado.id(), jurado.nombre(), jurado.email()));
    }
}
