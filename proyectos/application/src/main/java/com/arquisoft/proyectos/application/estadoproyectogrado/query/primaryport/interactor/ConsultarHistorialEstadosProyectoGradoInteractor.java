package com.arquisoft.proyectos.application.estadoproyectogrado.query.primaryport.interactor;

import com.arquisoft.proyectos.application.estadoproyectogrado.query.primaryport.model.ConsultarHistorialEstadosProyectoGradoQuery;
import com.arquisoft.proyectos.application.estadoproyectogrado.query.readmodel.EstadoProyectoGradoReadModel;
import com.arquisoft.shared.interactor.Interactor;

import java.util.List;

public interface ConsultarHistorialEstadosProyectoGradoInteractor
        extends Interactor<ConsultarHistorialEstadosProyectoGradoQuery, List<EstadoProyectoGradoReadModel>> {}
