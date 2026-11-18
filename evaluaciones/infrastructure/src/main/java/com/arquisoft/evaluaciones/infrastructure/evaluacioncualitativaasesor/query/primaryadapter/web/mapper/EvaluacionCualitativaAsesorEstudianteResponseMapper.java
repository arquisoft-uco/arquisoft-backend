package com.arquisoft.evaluaciones.infrastructure.evaluacioncualitativaasesor.query.primaryadapter.web.mapper;

import com.arquisoft.evaluaciones.application.evaluacioncualitativaasesor.query.readmodel.EvaluacionCualitativaAsesorEstudianteReadModel;
import com.arquisoft.evaluaciones.infrastructure.criterioitemcualitativoasesor.query.primaryadapter.web.mapper.CriterioItemCualitativoAsesorResponseMapper;
import com.arquisoft.evaluaciones.infrastructure.evaluacioncualitativaasesor.query.primaryadapter.web.dto.EvaluacionCualitativaAsesorEstudianteResponseDTO;
import com.arquisoft.evaluaciones.infrastructure.itemcualitativoasesor.query.primaryadapter.web.mapper.ItemCualitativoAsesorResponseMapper;

public final class EvaluacionCualitativaAsesorEstudianteResponseMapper {

    private EvaluacionCualitativaAsesorEstudianteResponseMapper() {}

    public static EvaluacionCualitativaAsesorEstudianteResponseDTO toResponse(EvaluacionCualitativaAsesorEstudianteReadModel readModel) {
        return new EvaluacionCualitativaAsesorEstudianteResponseDTO(
                readModel.id(),
                ItemCualitativoAsesorResponseMapper.toResponse(readModel.item()),
                CriterioItemCualitativoAsesorResponseMapper.toResponse(readModel.criterio()));
    }
}
