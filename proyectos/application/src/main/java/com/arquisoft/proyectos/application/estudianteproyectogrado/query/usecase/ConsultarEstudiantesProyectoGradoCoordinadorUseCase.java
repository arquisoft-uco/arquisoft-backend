package com.arquisoft.proyectos.application.estudianteproyectogrado.query.usecase;

import com.arquisoft.proyectos.application.estudianteproyectogrado.query.criteria.EstudianteProyectoGradoCoordinadorCriteria;
import com.arquisoft.proyectos.application.estudianteproyectogrado.query.readmodel.EstudianteProyectoGradoReadModel;
import com.arquisoft.shared.usecase.UseCase;

import java.util.List;

public interface ConsultarEstudiantesProyectoGradoCoordinadorUseCase
        extends UseCase<EstudianteProyectoGradoCoordinadorCriteria, List<EstudianteProyectoGradoReadModel>> {}
