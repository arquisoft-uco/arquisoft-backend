package com.arquisoft.mapas_ruta.application.planreal.query.primaryport.interactor;

import com.arquisoft.mapas_ruta.application.planreal.query.primaryport.model.ConsultarPlanesRealesProyectoGradoAsesorQuery;
import com.arquisoft.mapas_ruta.application.planreal.query.readmodel.PlanRealProyectoReadModel;
import com.arquisoft.shared.interactor.Interactor;
import com.arquisoft.shared.query.pagination.PaginatedResult;

public interface ConsultarPlanesRealesProyectoGradoAsesorInteractor
        extends Interactor<ConsultarPlanesRealesProyectoGradoAsesorQuery, PaginatedResult<PlanRealProyectoReadModel>> {}
