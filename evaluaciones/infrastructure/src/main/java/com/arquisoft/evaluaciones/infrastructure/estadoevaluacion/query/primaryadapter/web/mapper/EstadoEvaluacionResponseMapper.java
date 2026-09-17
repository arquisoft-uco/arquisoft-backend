package com.arquisoft.evaluaciones.infrastructure.estadoevaluacion.query.primaryadapter.web.mapper;

import com.arquisoft.evaluaciones.application.estadoevaluacion.query.readmodel.EstadoEvaluacionReadModel;
import com.arquisoft.evaluaciones.infrastructure.estadoevaluacion.query.primaryadapter.web.dto.EstadoEvaluacionResponseDTO;

public final class EstadoEvaluacionResponseMapper {

    private EstadoEvaluacionResponseMapper() {}

    public static EstadoEvaluacionResponseDTO toResponse(EstadoEvaluacionReadModel readModel) {
        return new EstadoEvaluacionResponseDTO(
                readModel.id(),
                readModel.nombre(),
                readModel.descripcion());
    }
}
