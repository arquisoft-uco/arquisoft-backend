package com.arquisoft.proyectos.application.estudianteproyectogrado.query.usecase;

import com.arquisoft.proyectos.application.estudianteproyectogrado.query.criteria.EstudianteAsesoradoProyectoGradoCriteria;
import com.arquisoft.proyectos.application.estudianteproyectogrado.query.readmodel.EstudianteAsesoradoProyectoGradoReadModel;
import com.arquisoft.shared.usecase.UseCase;

import java.util.List;

public interface ConsultarEstudiantesAsesoradosProyectoGradoUseCase
        extends UseCase<EstudianteAsesoradoProyectoGradoCriteria, List<EstudianteAsesoradoProyectoGradoReadModel>> {}
