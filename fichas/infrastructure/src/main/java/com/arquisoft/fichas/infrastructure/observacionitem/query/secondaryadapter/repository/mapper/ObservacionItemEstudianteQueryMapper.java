package com.arquisoft.fichas.infrastructure.observacionitem.query.secondaryadapter.repository.mapper;

import com.arquisoft.fichas.application.observacionitem.query.readmodel.ObservacionItemReadModel;
import com.arquisoft.fichas.infrastructure.observacionitem.query.secondaryadapter.repository.ObservacionItemEstudianteJpaQueryEntity;

public final class ObservacionItemEstudianteQueryMapper {

    private ObservacionItemEstudianteQueryMapper() {}

    public static ObservacionItemReadModel toReadModel(ObservacionItemEstudianteJpaQueryEntity entity) {
        return new ObservacionItemReadModel(
                entity.getId(),
                entity.getRevisionItemId(),
                entity.getObservacion(),
                entity.getEstadoObservacionRevisionId(),
                entity.getEstadoObservacionRevisionNombre());
    }
}
