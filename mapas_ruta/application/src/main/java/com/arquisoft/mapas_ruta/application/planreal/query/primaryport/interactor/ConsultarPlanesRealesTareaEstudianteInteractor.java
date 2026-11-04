package com.arquisoft.mapas_ruta.application.planreal.query.primaryport.interactor;

import com.arquisoft.mapas_ruta.application.planreal.query.primaryport.model.ConsultarPlanesRealesTareaEstudianteQuery;
import com.arquisoft.mapas_ruta.application.planreal.query.readmodel.PlanRealReadModel;
import com.arquisoft.shared.interactor.Interactor;
import com.arquisoft.shared.query.pagination.PaginatedResult;

public interface ConsultarPlanesRealesTareaEstudianteInteractor
        extends Interactor<ConsultarPlanesRealesTareaEstudianteQuery, PaginatedResult<PlanRealReadModel>> {}
