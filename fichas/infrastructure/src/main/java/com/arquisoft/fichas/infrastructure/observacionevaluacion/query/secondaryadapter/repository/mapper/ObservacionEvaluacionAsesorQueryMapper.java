package com.arquisoft.fichas.infrastructure.observacionevaluacion.query.secondaryadapter.repository.mapper;

import com.arquisoft.fichas.application.observacionevaluacion.query.readmodel.ObservacionEvaluacionReadModel;
import com.arquisoft.fichas.infrastructure.observacionevaluacion.query.secondaryadapter.repository.ObservacionEvaluacionAsesorJpaQueryEntity;

public final class ObservacionEvaluacionAsesorQueryMapper {

    private ObservacionEvaluacionAsesorQueryMapper() {}

    public static ObservacionEvaluacionReadModel toReadModel(ObservacionEvaluacionAsesorJpaQueryEntity entity) {
        return new ObservacionEvaluacionReadModel(
                entity.getId(),
                entity.getEvaluacionFichaPerfilId(),
                entity.getObservacion());
    }
}
