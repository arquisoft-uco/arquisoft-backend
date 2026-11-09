package com.arquisoft.proyectos.infrastructure.proyectogrado.query.secondaryadapter.repository.mapper;

import com.arquisoft.proyectos.application.proyectogrado.query.readmodel.CoordinadorProyectoGradoReadModel;
import com.arquisoft.proyectos.infrastructure.proyectogrado.query.secondaryadapter.repository.CoordinadorProyectoGradoJpaQueryEntity;

public final class CoordinadorProyectoGradoQueryMapper {

    private CoordinadorProyectoGradoQueryMapper() {}

    public static CoordinadorProyectoGradoReadModel toReadModel(CoordinadorProyectoGradoJpaQueryEntity entity) {
        return new CoordinadorProyectoGradoReadModel(
                entity.getProyectoGradoId(),
                entity.getTituloProyecto(),
                entity.getCoordinadorId(),
                entity.getIdentificador(),
                entity.getNombre(),
                entity.getEmail());
    }
}
