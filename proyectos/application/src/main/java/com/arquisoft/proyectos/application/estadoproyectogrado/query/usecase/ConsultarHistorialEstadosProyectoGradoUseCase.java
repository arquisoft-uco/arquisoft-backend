package com.arquisoft.proyectos.application.estadoproyectogrado.query.usecase;

import com.arquisoft.proyectos.application.estadoproyectogrado.query.criteria.HistorialEstadosProyectoGradoCriteria;
import com.arquisoft.proyectos.application.estadoproyectogrado.query.readmodel.EstadoProyectoGradoReadModel;
import com.arquisoft.shared.usecase.UseCase;

import java.util.List;

public interface ConsultarHistorialEstadosProyectoGradoUseCase
        extends UseCase<HistorialEstadosProyectoGradoCriteria, List<EstadoProyectoGradoReadModel>> {}
