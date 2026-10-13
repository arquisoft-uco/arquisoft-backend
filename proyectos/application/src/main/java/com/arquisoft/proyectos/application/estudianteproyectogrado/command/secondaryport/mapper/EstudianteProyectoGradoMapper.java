package com.arquisoft.proyectos.application.estudianteproyectogrado.command.secondaryport.mapper;

import com.arquisoft.proyectos.application.estudianteproyectogrado.command.secondaryport.entity.EstudianteProyectoGradoEntity;
import com.arquisoft.proyectos.domain.estudianteproyectogrado.EstudianteProyectoGradoDomain;

public final class EstudianteProyectoGradoMapper {

    private EstudianteProyectoGradoMapper() {}

    public static EstudianteProyectoGradoEntity toEntity(EstudianteProyectoGradoDomain vinculo) {
        return new EstudianteProyectoGradoEntity(vinculo.getId(), vinculo.getEstudiante(), vinculo.getProyectoGrado());
    }

    public static EstudianteProyectoGradoDomain toDomain(EstudianteProyectoGradoEntity entity) {
        return EstudianteProyectoGradoDomain.reconstruir(entity.id(), entity.estudiante(), entity.proyectoGrado());
    }
}
