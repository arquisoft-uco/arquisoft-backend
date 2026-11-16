package com.arquisoft.biblioteca.application.estudianteproyectogrado.command.primaryport.mapper;

import com.arquisoft.biblioteca.application.estudianteproyectogrado.command.primaryport.model.AgregarEstudiantesProyectoGradoCommand;
import com.arquisoft.biblioteca.application.proyectogrado.command.primaryport.mapper.AgregarProyectoGradoMapper;
import com.arquisoft.biblioteca.domain.estudianteproyectogrado.AgregacionEstudiantesProyectoGradoDomain;
import com.arquisoft.biblioteca.domain.estudianteproyectogrado.EstudianteProyectoGradoDomain;

public final class AgregarEstudiantesProyectoGradoMapper {

    private AgregarEstudiantesProyectoGradoMapper() {}

    public static AgregacionEstudiantesProyectoGradoDomain toDomain(AgregarEstudiantesProyectoGradoCommand command) {
        var proyecto = AgregarProyectoGradoMapper.toDomain(
                command.proyectoGrado(), command.tituloProyecto(), command.ocurridoEn());
        var vinculos = command.estudiantes().stream()
                .map(estudiante -> EstudianteProyectoGradoDomain.crear(
                        estudiante, command.proyectoGrado(), command.ocurridoEn()))
                .toList();
        return AgregacionEstudiantesProyectoGradoDomain.crear(proyecto, vinculos);
    }
}
