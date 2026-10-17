package com.arquisoft.evaluaciones.infrastructure.evaluacionjurado.query.primaryadapter.web.mapper;

import com.arquisoft.evaluaciones.application.evaluacionjurado.query.readmodel.EvaluacionAsignadaJuradoReadModel;
import com.arquisoft.evaluaciones.infrastructure.evaluacionjurado.query.primaryadapter.web.dto.EvaluacionAsignadaJuradoResponseDTO;

public final class EvaluacionAsignadaJuradoResponseMapper {

    private EvaluacionAsignadaJuradoResponseMapper() {}

    public static EvaluacionAsignadaJuradoResponseDTO toResponse(EvaluacionAsignadaJuradoReadModel readModel) {
        return new EvaluacionAsignadaJuradoResponseDTO(
                readModel.id(),
                new EvaluacionAsignadaJuradoResponseDTO.EvaluacionDTO(
                        readModel.evaluacion().id(),
                        new EvaluacionAsignadaJuradoResponseDTO.EstadoDTO(
                                readModel.evaluacion().estado().id(),
                                readModel.evaluacion().estado().nombre())),
                new EvaluacionAsignadaJuradoResponseDTO.EntregableDTO(
                        readModel.entregable().id(),
                        readModel.entregable().proyecto(),
                        readModel.entregable().version()));
    }
}
