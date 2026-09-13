package com.arquisoft.evaluaciones.infrastructure.evaluacioncuantitativajurado.query.primaryadapter.web.mapper;

import com.arquisoft.evaluaciones.application.evaluacioncuantitativajurado.query.readmodel.EvaluacionCuantitativaJuradoReadModel;
import com.arquisoft.evaluaciones.infrastructure.evaluacioncuantitativajurado.query.primaryadapter.web.dto.EvaluacionCuantitativaJuradoResponseDTO;

public final class EvaluacionCuantitativaJuradoResponseMapper {

    private EvaluacionCuantitativaJuradoResponseMapper() {}

    public static EvaluacionCuantitativaJuradoResponseDTO toResponse(EvaluacionCuantitativaJuradoReadModel readModel) {
        return new EvaluacionCuantitativaJuradoResponseDTO(
                readModel.id(),
                readModel.puntaje(),
                new EvaluacionCuantitativaJuradoResponseDTO.ItemDTO(
                        readModel.item().id(),
                        readModel.item().nombre(),
                        readModel.item().descripcion(),
                        readModel.item().categoriaId(),
                        readModel.item().valor()));
    }
}
