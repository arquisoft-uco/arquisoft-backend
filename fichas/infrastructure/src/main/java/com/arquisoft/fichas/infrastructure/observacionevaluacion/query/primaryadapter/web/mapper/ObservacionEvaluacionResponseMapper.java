package com.arquisoft.fichas.infrastructure.observacionevaluacion.query.primaryadapter.web.mapper;

import com.arquisoft.fichas.application.observacionevaluacion.query.readmodel.ObservacionEvaluacionReadModel;
import com.arquisoft.fichas.infrastructure.observacionevaluacion.query.primaryadapter.web.dto.ObservacionEvaluacionResponseDTO;

public final class ObservacionEvaluacionResponseMapper {

    private ObservacionEvaluacionResponseMapper() {}

    public static ObservacionEvaluacionResponseDTO toResponse(ObservacionEvaluacionReadModel readModel) {
        return new ObservacionEvaluacionResponseDTO(
                readModel.id(),
                readModel.evaluacionFichaPerfil(),
                readModel.observacion());
    }
}
