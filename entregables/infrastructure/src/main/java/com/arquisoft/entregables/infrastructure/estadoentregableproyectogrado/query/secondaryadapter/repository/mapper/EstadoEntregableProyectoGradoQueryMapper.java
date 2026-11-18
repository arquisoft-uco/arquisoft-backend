package com.arquisoft.entregables.infrastructure.estadoentregableproyectogrado.query.secondaryadapter.repository.mapper;

import com.arquisoft.entregables.application.estadoentregableproyectogrado.query.readmodel.EstadoEntregableProyectoGradoReadModel;
import com.arquisoft.entregables.infrastructure.estadoentregableproyectogrado.query.secondaryadapter.repository.EstadoEntregableProyectoGradoJpaQueryEntity;

public final class EstadoEntregableProyectoGradoQueryMapper {

    private EstadoEntregableProyectoGradoQueryMapper() {}

    public static EstadoEntregableProyectoGradoReadModel toReadModel(
            EstadoEntregableProyectoGradoJpaQueryEntity entity) {
        return new EstadoEntregableProyectoGradoReadModel(
                entity.getId(),
                entity.getEstadoEntregableId(),
                entity.getEstadoEntregableNombre(),
                entity.getFechaActualizacion());
    }
}
