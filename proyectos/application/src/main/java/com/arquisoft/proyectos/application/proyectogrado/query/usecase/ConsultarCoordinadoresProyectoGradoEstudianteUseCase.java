package com.arquisoft.proyectos.application.proyectogrado.query.usecase;

import com.arquisoft.proyectos.application.proyectogrado.query.criteria.CoordinadorProyectoGradoEstudianteCriteria;
import com.arquisoft.proyectos.application.proyectogrado.query.readmodel.CoordinadorProyectoGradoReadModel;
import com.arquisoft.shared.usecase.UseCase;

import java.util.List;

public interface ConsultarCoordinadoresProyectoGradoEstudianteUseCase
        extends UseCase<CoordinadorProyectoGradoEstudianteCriteria, List<CoordinadorProyectoGradoReadModel>> {}
