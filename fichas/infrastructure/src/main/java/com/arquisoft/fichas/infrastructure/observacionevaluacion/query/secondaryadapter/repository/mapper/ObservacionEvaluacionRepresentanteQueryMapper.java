package com.arquisoft.fichas.infrastructure.observacionevaluacion.query.secondaryadapter.repository.mapper;

import com.arquisoft.fichas.application.observacionevaluacion.query.readmodel.ObservacionEvaluacionReadModel;
import com.arquisoft.fichas.infrastructure.observacionevaluacion.query.secondaryadapter.repository.ObservacionEvaluacionRepresentanteJpaQueryEntity;

public final class ObservacionEvaluacionRepresentanteQueryMapper {

    private ObservacionEvaluacionRepresentanteQueryMapper() {}

    public static ObservacionEvaluacionReadModel toReadModel(ObservacionEvaluacionRepresentanteJpaQueryEntity entity) {
        return new ObservacionEvaluacionReadModel(
                entity.getId(),
                entity.getEvaluacionFichaPerfilId(),
                entity.getObservacion());
    }
}
