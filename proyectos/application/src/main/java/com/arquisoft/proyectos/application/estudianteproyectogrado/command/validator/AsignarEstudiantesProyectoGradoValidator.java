package com.arquisoft.proyectos.application.estudianteproyectogrado.command.validator;

import com.arquisoft.proyectos.domain.estadoproyectogrado.EstadoProyectoGradoDomain;
import com.arquisoft.proyectos.domain.estudiante.EstudianteDomain;
import com.arquisoft.proyectos.domain.estudianteproyectogrado.AgregacionEstudiantesProyectoGradoDomain;
import com.arquisoft.proyectos.domain.proyectogrado.ProyectoGradoDomain;

import java.util.List;
import java.util.UUID;

public interface AsignarEstudiantesProyectoGradoValidator {

    void validar(AgregacionEstudiantesProyectoGradoDomain entrada, ProyectoGradoDomain proyecto,
                 EstadoProyectoGradoDomain estadoActual, List<EstudianteDomain> estudiantesVigentes,
                 List<UUID> yaVinculados, long vinculadosActuales);
}
