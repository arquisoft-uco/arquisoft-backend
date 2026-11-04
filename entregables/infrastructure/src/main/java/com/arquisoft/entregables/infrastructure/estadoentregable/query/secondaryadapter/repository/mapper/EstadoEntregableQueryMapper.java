package com.arquisoft.entregables.infrastructure.estadoentregable.query.secondaryadapter.repository.mapper;

import com.arquisoft.entregables.application.estadoentregable.query.readmodel.EstadoEntregableReadModel;
import com.arquisoft.entregables.infrastructure.estadoentregable.query.secondaryadapter.repository.EstadoEntregableJpaQueryEntity;

public final class EstadoEntregableQueryMapper {

    private EstadoEntregableQueryMapper() {}

    public static EstadoEntregableReadModel toReadModel(EstadoEntregableJpaQueryEntity entity) {
        return new EstadoEntregableReadModel(
                entity.getId(),
                entity.getNombre(),
                entity.getDescripcion());
    }
}
