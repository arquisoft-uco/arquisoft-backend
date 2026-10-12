package com.arquisoft.fichas.infrastructure.observacionitem.query.secondaryadapter.repository.mapper;

import com.arquisoft.fichas.application.observacionitem.query.readmodel.ObservacionItemReadModel;
import com.arquisoft.fichas.infrastructure.observacionitem.query.secondaryadapter.repository.ObservacionItemJpaQueryEntity;

public final class ObservacionItemQueryMapper {

    private ObservacionItemQueryMapper() {}

    public static ObservacionItemReadModel toReadModel(ObservacionItemJpaQueryEntity entity) {
        return new ObservacionItemReadModel(
                entity.getId(),
                entity.getRevisionItemId(),
                entity.getObservacion(),
                entity.getEstadoObservacionRevisionId(),
                entity.getEstadoObservacionRevisionNombre());
    }
}
