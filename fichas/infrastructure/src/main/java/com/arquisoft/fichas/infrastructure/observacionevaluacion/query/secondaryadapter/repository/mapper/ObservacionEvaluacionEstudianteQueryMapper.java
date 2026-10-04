package com.arquisoft.fichas.infrastructure.observacionevaluacion.query.secondaryadapter.repository.mapper;

import com.arquisoft.fichas.application.observacionevaluacion.query.readmodel.ObservacionEvaluacionReadModel;
import com.arquisoft.fichas.infrastructure.observacionevaluacion.query.secondaryadapter.repository.ObservacionEvaluacionEstudianteJpaQueryEntity;

public final class ObservacionEvaluacionEstudianteQueryMapper {

    private ObservacionEvaluacionEstudianteQueryMapper() {}

    public static ObservacionEvaluacionReadModel toReadModel(ObservacionEvaluacionEstudianteJpaQueryEntity entity) {
        return new ObservacionEvaluacionReadModel(
                entity.getId(),
                entity.getEvaluacionFichaPerfilId(),
                entity.getObservacion());
    }
}
