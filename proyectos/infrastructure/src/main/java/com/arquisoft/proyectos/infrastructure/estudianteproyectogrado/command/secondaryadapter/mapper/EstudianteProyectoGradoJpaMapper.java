package com.arquisoft.proyectos.infrastructure.estudianteproyectogrado.command.secondaryadapter.mapper;

import com.arquisoft.proyectos.application.estudianteproyectogrado.command.secondaryport.entity.EstudianteProyectoGradoEntity;
import com.arquisoft.proyectos.infrastructure.estudianteproyectogrado.command.secondaryadapter.entity.EstudianteProyectoGradoJpaEntity;

public final class EstudianteProyectoGradoJpaMapper {

    private EstudianteProyectoGradoJpaMapper() {}

    public static EstudianteProyectoGradoJpaEntity toJpaEntity(EstudianteProyectoGradoEntity entity) {
        return EstudianteProyectoGradoJpaEntity.builder()
                .id(entity.id())
                .estudianteId(entity.estudiante())
                .proyectoGradoId(entity.proyectoGrado())
                .build();
    }
}
