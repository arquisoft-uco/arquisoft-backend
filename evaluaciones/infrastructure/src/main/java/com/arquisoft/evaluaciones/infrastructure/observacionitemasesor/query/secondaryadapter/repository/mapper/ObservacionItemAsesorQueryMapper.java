package com.arquisoft.evaluaciones.infrastructure.observacionitemasesor.query.secondaryadapter.repository.mapper;

import com.arquisoft.evaluaciones.application.observacionitemasesor.query.readmodel.ObservacionItemAsesorReadModel;
import com.arquisoft.evaluaciones.infrastructure.observacionitemasesor.query.secondaryadapter.repository.ObservacionItemAsesorJpaQueryEntity;

public final class ObservacionItemAsesorQueryMapper {

    private ObservacionItemAsesorQueryMapper() {}

    public static ObservacionItemAsesorReadModel toReadModel(ObservacionItemAsesorJpaQueryEntity entity) {
        return new ObservacionItemAsesorReadModel(entity.getId(), entity.getDescripcion());
    }
}
