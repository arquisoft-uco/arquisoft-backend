package com.arquisoft.proyectos.application.estudianteproyectogrado.command.validator;

import com.arquisoft.proyectos.domain.estudiante.EstudianteDomain;
import com.arquisoft.proyectos.domain.estudianteproyectogrado.AgregacionEstudiantesProyectoGradoDomain;
import com.arquisoft.proyectos.domain.proyectogrado.ProyectoGradoDomain;

import java.util.List;

public interface AsignarEstudiantesProyectoGradoValidator {

    void validar(AgregacionEstudiantesProyectoGradoDomain entrada, ProyectoGradoDomain proyecto,
                 List<EstudianteDomain> estudiantesVigentes, long vinculadosActuales);
}
