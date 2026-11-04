package com.arquisoft.proyectos.infrastructure.estadoasesor.query.secondaryadapter.repository.mapper;

import com.arquisoft.proyectos.application.estadoasesor.query.readmodel.EstadoAsesorReadModel;
import com.arquisoft.proyectos.infrastructure.estadoasesor.query.secondaryadapter.repository.EstadoAsesorJpaQueryEntity;

public final class EstadoAsesorQueryMapper {

    private EstadoAsesorQueryMapper() {}

    public static EstadoAsesorReadModel toReadModel(EstadoAsesorJpaQueryEntity entity) {
        return new EstadoAsesorReadModel(
                entity.getId(),
                entity.getNombre(),
                entity.getDescripcion());
    }
}
