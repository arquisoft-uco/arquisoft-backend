package com.arquisoft.proyectos.infrastructure.estadoproyectogrado.command.secondaryadapter.mapper;

import com.arquisoft.proyectos.application.estadoproyectogrado.command.secondaryport.entity.EstadoProyectoGradoEntity;
import com.arquisoft.proyectos.infrastructure.estadoproyectogrado.command.secondaryadapter.entity.EstadoProyectoGradoJpaEntity;

public final class EstadoProyectoGradoJpaMapper {

    private EstadoProyectoGradoJpaMapper() {}

    public static EstadoProyectoGradoEntity toEntity(EstadoProyectoGradoJpaEntity jpaEntity) {
        return new EstadoProyectoGradoEntity(
                jpaEntity.getId(),
                jpaEntity.getProyectoGradoId(),
                jpaEntity.getEstadoProyectoId(),
                jpaEntity.getFechaActualizacion());
    }

    public static EstadoProyectoGradoJpaEntity toJpaEntity(EstadoProyectoGradoEntity entity) {
        return EstadoProyectoGradoJpaEntity.builder()
                .id(entity.id())
                .proyectoGradoId(entity.proyectoGrado())
                .estadoProyectoId(entity.estadoProyecto())
                .fechaActualizacion(entity.fechaActualizacion())
                .build();
    }
}
