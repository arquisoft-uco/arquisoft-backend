package com.arquisoft.artefactos.infrastructure.estudianteproyectogrado.command.secondaryadapter.mapper;

import com.arquisoft.artefactos.application.estudianteproyectogrado.command.secondaryport.entity.PertenenciaProyectoGradoEntity;
import com.arquisoft.artefactos.infrastructure.estudianteproyectogrado.command.secondaryadapter.repository.EstudianteProyectoGradoCommandRepository.PertenenciaProyeccion;

public final class EstudianteProyectoGradoJpaMapper {

    private EstudianteProyectoGradoJpaMapper() {}

    public static PertenenciaProyectoGradoEntity toEntity(PertenenciaProyeccion proyeccion) {
        return new PertenenciaProyectoGradoEntity(proyeccion.artefacto(), proyeccion.estudiante());
    }
}
