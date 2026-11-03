package com.arquisoft.evaluaciones.infrastructure.evaluacioncualitativaasesor.query.primaryadapter.web.mapper;

import com.arquisoft.evaluaciones.application.evaluacioncualitativaasesor.query.readmodel.EvaluacionCualitativaAsesorReadModel;
import com.arquisoft.evaluaciones.infrastructure.criterioitemcualitativoasesor.query.primaryadapter.web.mapper.CriterioItemCualitativoAsesorResponseMapper;
import com.arquisoft.evaluaciones.infrastructure.evaluacioncualitativaasesor.query.primaryadapter.web.dto.EvaluacionCualitativaAsesorResponseDTO;
import com.arquisoft.evaluaciones.infrastructure.itemcualitativoasesor.query.primaryadapter.web.mapper.ItemCualitativoAsesorResponseMapper;

public final class EvaluacionCualitativaAsesorResponseMapper {

    private EvaluacionCualitativaAsesorResponseMapper() {}

    public static EvaluacionCualitativaAsesorResponseDTO toResponse(EvaluacionCualitativaAsesorReadModel readModel) {
        return new EvaluacionCualitativaAsesorResponseDTO(
                readModel.id(),
                ItemCualitativoAsesorResponseMapper.toResponse(readModel.item()),
                CriterioItemCualitativoAsesorResponseMapper.toResponse(readModel.criterio()));
    }
}
