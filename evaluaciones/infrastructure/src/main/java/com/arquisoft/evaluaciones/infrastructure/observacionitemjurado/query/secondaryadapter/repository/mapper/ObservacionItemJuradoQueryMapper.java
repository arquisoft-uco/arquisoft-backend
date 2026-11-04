package com.arquisoft.evaluaciones.infrastructure.observacionitemjurado.query.secondaryadapter.repository.mapper;

import com.arquisoft.evaluaciones.application.observacionitemjurado.query.readmodel.ObservacionItemJuradoReadModel;
import com.arquisoft.evaluaciones.infrastructure.observacionitemjurado.query.secondaryadapter.repository.ObservacionItemJuradoJpaQueryEntity;

public final class ObservacionItemJuradoQueryMapper {

    private ObservacionItemJuradoQueryMapper() {}

    public static ObservacionItemJuradoReadModel toReadModel(ObservacionItemJuradoJpaQueryEntity entity) {
        return new ObservacionItemJuradoReadModel(entity.getId(), entity.getDescripcion());
    }
}
