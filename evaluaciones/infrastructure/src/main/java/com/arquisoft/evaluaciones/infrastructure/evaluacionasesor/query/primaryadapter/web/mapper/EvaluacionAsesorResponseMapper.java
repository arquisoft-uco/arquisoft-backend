package com.arquisoft.evaluaciones.infrastructure.evaluacionasesor.query.primaryadapter.web.mapper;

import com.arquisoft.evaluaciones.application.evaluacionasesor.query.readmodel.EvaluacionAsesorReadModel;
import com.arquisoft.evaluaciones.infrastructure.evaluacionasesor.query.primaryadapter.web.dto.EvaluacionAsesorResponseDTO;

public final class EvaluacionAsesorResponseMapper {

    private EvaluacionAsesorResponseMapper() {}

    public static EvaluacionAsesorResponseDTO toResponse(EvaluacionAsesorReadModel readModel) {
        var asesor = readModel.asesor();
        return new EvaluacionAsesorResponseDTO(
                readModel.id(),
                new EvaluacionAsesorResponseDTO.Asesor(asesor.id(), asesor.nombre(), asesor.email()));
    }
}
