package com.arquisoft.proyectos.infrastructure.proyectogrado.query.secondaryadapter.repository.mapper;

import com.arquisoft.proyectos.application.estadoproyecto.query.readmodel.EstadoProyectoReadModel;
import com.arquisoft.proyectos.application.proyectogrado.query.readmodel.ProyectoGradoReadModel;
import com.arquisoft.proyectos.infrastructure.proyectogrado.query.secondaryadapter.repository.ProyectoGradoEstudianteJpaQueryEntity;
import com.arquisoft.proyectos.infrastructure.proyectogrado.query.secondaryadapter.repository.ProyectoGradoListadoJpaQueryEntity;

public final class ProyectoGradoQueryMapper {

    private ProyectoGradoQueryMapper() {}

    public static ProyectoGradoReadModel toReadModel(ProyectoGradoListadoJpaQueryEntity entity) {
        return new ProyectoGradoReadModel(
                entity.getId(),
                entity.getFichaPerfilId(),
                entity.getTituloProyecto(),
                new EstadoProyectoReadModel(
                        entity.getEstadoId(),
                        entity.getEstadoNombre()));
    }

    public static ProyectoGradoReadModel toReadModel(ProyectoGradoEstudianteJpaQueryEntity entity) {
        return new ProyectoGradoReadModel(
                entity.getProyectoGradoId(),
                entity.getFichaPerfilId(),
                entity.getTituloProyecto(),
                new EstadoProyectoReadModel(
                        entity.getEstadoId(),
                        entity.getEstadoNombre()));
    }
}
