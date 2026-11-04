package com.arquisoft.proyectos.application.estudianteproyectogrado.command.primaryport.mapper;

import com.arquisoft.proyectos.application.estudianteproyectogrado.command.primaryport.model.AsignarEstudiantesProyectoGradoCommand;
import com.arquisoft.proyectos.domain.estudianteproyectogrado.AgregacionEstudiantesProyectoGradoDomain;
import com.arquisoft.proyectos.domain.estudianteproyectogrado.EstudianteProyectoGradoDomain;

import java.util.List;
import java.util.UUID;

public final class AsignarEstudiantesProyectoGradoMapper {

    private AsignarEstudiantesProyectoGradoMapper() {}

    public static AgregacionEstudiantesProyectoGradoDomain toDomain(AsignarEstudiantesProyectoGradoCommand command) {
        return toDomain(command.proyectoGrado(), command.coordinador(), command.estudiantes());
    }

    public static AgregacionEstudiantesProyectoGradoDomain toDomain(UUID proyectoGrado, UUID coordinador,
                                                                   List<UUID> estudiantes) {
        var relaciones = EstudianteProyectoGradoDomain.crear(proyectoGrado, estudiantes);

        return AgregacionEstudiantesProyectoGradoDomain.crear(relaciones, coordinador);
    }
}
