package com.arquisoft.proyectos.infrastructure.estadoproyectogrado.query.secondaryadapter.repository.mapper;

import com.arquisoft.proyectos.application.estadoproyectogrado.query.readmodel.EstadoProyectoGradoReadModel;
import com.arquisoft.proyectos.infrastructure.estadoproyectogrado.query.secondaryadapter.repository.EstadoProyectoGradoJpaQueryEntity;

public final class EstadoProyectoGradoQueryMapper {

    private EstadoProyectoGradoQueryMapper() {}

    public static EstadoProyectoGradoReadModel toReadModel(EstadoProyectoGradoJpaQueryEntity entity) {
        return new EstadoProyectoGradoReadModel(
                entity.getId(),
                entity.getEstadoProyecto(),
                entity.getEstadoProyectoNombre(),
                entity.getFechaActualizacion());
    }
}
