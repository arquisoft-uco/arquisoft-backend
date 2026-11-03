package com.arquisoft.evaluaciones.infrastructure.evaluacioncuantitativaasesor.query.primaryadapter.web.mapper;

import com.arquisoft.evaluaciones.application.evaluacioncuantitativaasesor.query.readmodel.EvaluacionCuantitativaAsesorReadModel;
import com.arquisoft.evaluaciones.infrastructure.evaluacioncuantitativaasesor.query.primaryadapter.web.dto.EvaluacionCuantitativaAsesorResponseDTO;
import com.arquisoft.evaluaciones.infrastructure.itemcuantitativoasesor.query.primaryadapter.web.mapper.ItemCuantitativoAsesorResponseMapper;

public final class EvaluacionCuantitativaAsesorResponseMapper {

    private EvaluacionCuantitativaAsesorResponseMapper() {}

    public static EvaluacionCuantitativaAsesorResponseDTO toResponse(EvaluacionCuantitativaAsesorReadModel readModel) {
        return new EvaluacionCuantitativaAsesorResponseDTO(
                readModel.id(),
                readModel.puntaje(),
                ItemCuantitativoAsesorResponseMapper.toResponse(readModel.item()));
    }
}
