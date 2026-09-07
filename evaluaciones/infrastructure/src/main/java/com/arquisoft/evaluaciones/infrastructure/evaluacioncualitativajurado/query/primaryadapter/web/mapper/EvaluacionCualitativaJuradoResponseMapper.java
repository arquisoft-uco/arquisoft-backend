package com.arquisoft.evaluaciones.infrastructure.evaluacioncualitativajurado.query.primaryadapter.web.mapper;

import com.arquisoft.evaluaciones.application.evaluacioncualitativajurado.query.readmodel.EvaluacionCualitativaJuradoReadModel;
import com.arquisoft.evaluaciones.infrastructure.evaluacioncualitativajurado.query.primaryadapter.web.dto.EvaluacionCualitativaJuradoResponseDTO;

public final class EvaluacionCualitativaJuradoResponseMapper {

    private EvaluacionCualitativaJuradoResponseMapper() {}

    public static EvaluacionCualitativaJuradoResponseDTO toResponse(EvaluacionCualitativaJuradoReadModel readModel) {
        return new EvaluacionCualitativaJuradoResponseDTO(
                readModel.id(),
                new EvaluacionCualitativaJuradoResponseDTO.ItemDTO(
                        readModel.item().id(),
                        readModel.item().nombre(),
                        readModel.item().descripcion()),
                new EvaluacionCualitativaJuradoResponseDTO.CriterioDTO(
                        readModel.criterio().id(),
                        readModel.criterio().nombre(),
                        readModel.criterio().descripcion()));
    }
}
