package com.arquisoft.evaluaciones.infrastructure.evaluacioncualitativaasesor.query.primaryadapter.web.mapper;

import com.arquisoft.evaluaciones.application.evaluacioncualitativaasesor.query.readmodel.EvaluacionCualitativaAsesorReadModel;
import com.arquisoft.evaluaciones.infrastructure.evaluacioncualitativaasesor.query.primaryadapter.web.dto.EvaluacionCualitativaAsesorResponseDTO;

public final class EvaluacionCualitativaAsesorResponseMapper {

    private EvaluacionCualitativaAsesorResponseMapper() {}

    public static EvaluacionCualitativaAsesorResponseDTO toResponse(EvaluacionCualitativaAsesorReadModel readModel) {
        return new EvaluacionCualitativaAsesorResponseDTO(
                readModel.id(),
                new EvaluacionCualitativaAsesorResponseDTO.ItemDTO(
                        readModel.item().id(),
                        readModel.item().nombre(),
                        readModel.item().descripcion()),
                new EvaluacionCualitativaAsesorResponseDTO.CriterioDTO(
                        readModel.criterio().id(),
                        readModel.criterio().nombre(),
                        readModel.criterio().descripcion()));
    }
}
