package com.arquisoft.entregables.application.estudianteproyectogrado.command.primaryport.mapper;

import com.arquisoft.entregables.application.estudianteproyectogrado.command.primaryport.model.AgregarEstudiantesProyectoGradoCommand;
import com.arquisoft.entregables.domain.estudianteproyectogrado.AgregacionEstudiantesProyectoGradoDomain;
import com.arquisoft.entregables.domain.estudianteproyectogrado.EstudianteProyectoGradoDomain;
import com.arquisoft.shared.util.UtilUUID;

public final class AgregarEstudiantesProyectoGradoMapper {

    private AgregarEstudiantesProyectoGradoMapper() {}

    public static AgregacionEstudiantesProyectoGradoDomain toDomain(AgregarEstudiantesProyectoGradoCommand command) {
        var vinculos = command.estudiantes().stream()
                .map(estudiante -> EstudianteProyectoGradoDomain.crear(
                        UtilUUID.generarUUIDDesdeTexto(estudiante.id()), estudiante.nombre(), estudiante.email(),
                        command.proyectoGrado(), command.ocurridoEn()))
                .toList();
        return AgregacionEstudiantesProyectoGradoDomain.crear(command.proyectoGrado(), vinculos);
    }
}
