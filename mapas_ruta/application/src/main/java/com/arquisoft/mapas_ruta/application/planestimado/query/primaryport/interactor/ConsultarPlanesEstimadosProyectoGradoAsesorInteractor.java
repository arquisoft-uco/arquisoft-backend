package com.arquisoft.mapas_ruta.application.planestimado.query.primaryport.interactor;

import com.arquisoft.mapas_ruta.application.planestimado.query.primaryport.model.ConsultarPlanesEstimadosProyectoGradoAsesorQuery;
import com.arquisoft.mapas_ruta.application.planestimado.query.readmodel.PlanEstimadoProyectoReadModel;
import com.arquisoft.shared.interactor.Interactor;
import com.arquisoft.shared.query.pagination.PaginatedResult;

public interface ConsultarPlanesEstimadosProyectoGradoAsesorInteractor
        extends Interactor<ConsultarPlanesEstimadosProyectoGradoAsesorQuery, PaginatedResult<PlanEstimadoProyectoReadModel>> {}
