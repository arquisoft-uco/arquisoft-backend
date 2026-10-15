package com.arquisoft.proyectos.application.proyectogrado.query.usecase;

import com.arquisoft.proyectos.application.proyectogrado.query.criteria.ProyectoGradoEstudianteCriteria;
import com.arquisoft.proyectos.application.proyectogrado.query.readmodel.ProyectoGradoReadModel;
import com.arquisoft.shared.usecase.UseCase;

import java.util.List;

public interface ConsultarProyectosGradoEstudianteUseCase
        extends UseCase<ProyectoGradoEstudianteCriteria, List<ProyectoGradoReadModel>> {}
