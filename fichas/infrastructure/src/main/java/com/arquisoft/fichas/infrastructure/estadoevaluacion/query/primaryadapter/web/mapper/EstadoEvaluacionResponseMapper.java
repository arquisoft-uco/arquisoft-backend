package com.arquisoft.fichas.infrastructure.estadoevaluacion.query.primaryadapter.web.mapper;

import com.arquisoft.fichas.application.estadoevaluacion.query.readmodel.EstadoEvaluacionReadModel;
import com.arquisoft.fichas.infrastructure.estadoevaluacion.query.primaryadapter.web.dto.EstadoEvaluacionResponseDTO;

public final class EstadoEvaluacionResponseMapper {

    private EstadoEvaluacionResponseMapper() {}

    public static EstadoEvaluacionResponseDTO toResponse(EstadoEvaluacionReadModel readModel) {
        return new EstadoEvaluacionResponseDTO(
                readModel.id(),
                readModel.nombre(),
                readModel.descripcion());
    }
}
