package com.arquisoft.artefactos.application.estudianteproyectogrado.command.primaryport.mapper;

import com.arquisoft.artefactos.application.estudianteproyectogrado.command.primaryport.model.AgregarEstudiantesProyectoGradoCommand;
import com.arquisoft.artefactos.domain.estudianteproyectogrado.AgregacionEstudiantesProyectoGradoDomain;
import com.arquisoft.artefactos.domain.estudianteproyectogrado.EstudianteProyectoGradoDomain;

public final class AgregarEstudiantesProyectoGradoMapper {

    private AgregarEstudiantesProyectoGradoMapper() {}

    public static AgregacionEstudiantesProyectoGradoDomain toDomain(AgregarEstudiantesProyectoGradoCommand command) {
        var vinculos = command.estudiantes().stream()
                .map(estudiante -> EstudianteProyectoGradoDomain.crear(
                        estudiante, command.proyectoGrado(), command.ocurridoEn()))
                .toList();
        return AgregacionEstudiantesProyectoGradoDomain.crear(vinculos);
    }
}
