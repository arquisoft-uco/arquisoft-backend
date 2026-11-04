package com.arquisoft.proyectos.application.estadoproyectogrado.query.primaryport.interactor;

import com.arquisoft.proyectos.application.estadoproyectogrado.query.primaryport.model.ConsultarEstadosProyectoGradoEstudianteQuery;
import com.arquisoft.proyectos.application.estadoproyectogrado.query.readmodel.EstadoProyectoGradoReadModel;
import com.arquisoft.shared.interactor.Interactor;

import java.util.List;

public interface ConsultarEstadosProyectoGradoEstudianteInteractor
        extends Interactor<ConsultarEstadosProyectoGradoEstudianteQuery, List<EstadoProyectoGradoReadModel>> {}
