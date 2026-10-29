package com.arquisoft.mapas_ruta.application.planestimado.query.primaryport.interactor;

import com.arquisoft.mapas_ruta.application.planestimado.query.primaryport.model.ConsultarPlanesEstimadosTareaEstudianteQuery;
import com.arquisoft.mapas_ruta.application.planestimado.query.readmodel.PlanEstimadoReadModel;
import com.arquisoft.shared.interactor.Interactor;
import com.arquisoft.shared.query.pagination.PaginatedResult;

public interface ConsultarPlanesEstimadosTareaEstudianteInteractor
        extends Interactor<ConsultarPlanesEstimadosTareaEstudianteQuery, PaginatedResult<PlanEstimadoReadModel>> {}
