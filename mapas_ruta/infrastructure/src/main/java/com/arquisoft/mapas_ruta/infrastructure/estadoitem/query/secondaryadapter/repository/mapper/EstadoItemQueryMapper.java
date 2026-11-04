package com.arquisoft.mapas_ruta.infrastructure.estadoitem.query.secondaryadapter.repository.mapper;

import com.arquisoft.mapas_ruta.application.estadoitem.query.readmodel.EstadoItemReadModel;
import com.arquisoft.mapas_ruta.infrastructure.estadoitem.query.secondaryadapter.repository.EstadoItemJpaQueryEntity;

public final class EstadoItemQueryMapper {

    private EstadoItemQueryMapper() {}

    public static EstadoItemReadModel toReadModel(EstadoItemJpaQueryEntity entity) {
        return new EstadoItemReadModel(
                entity.getId(),
                entity.getNombre(),
                entity.getDescripcion());
    }
}
