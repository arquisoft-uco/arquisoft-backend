package com.arquisoft.proyectos.application.proyectogrado.query.primaryport.interactor;

import com.arquisoft.proyectos.application.proyectogrado.query.primaryport.model.ConsultarProyectosGradoCoordinadorQuery;
import com.arquisoft.proyectos.application.proyectogrado.query.readmodel.ProyectoGradoReadModel;
import com.arquisoft.shared.interactor.Interactor;
import com.arquisoft.shared.query.pagination.PaginatedResult;

public interface ConsultarProyectosGradoCoordinadorInteractor
        extends Interactor<ConsultarProyectosGradoCoordinadorQuery, PaginatedResult<ProyectoGradoReadModel>> {}
