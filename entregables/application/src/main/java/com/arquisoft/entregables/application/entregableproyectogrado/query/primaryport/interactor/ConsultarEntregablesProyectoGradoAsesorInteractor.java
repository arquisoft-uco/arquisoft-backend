package com.arquisoft.entregables.application.entregableproyectogrado.query.primaryport.interactor;

import com.arquisoft.entregables.application.entregableproyectogrado.query.readmodel.EntregableProyectoGradoReadModel;
import com.arquisoft.shared.interactor.Interactor;
import com.arquisoft.shared.query.ConsultaCriteriaQuery;
import com.arquisoft.shared.query.pagination.PaginatedResult;

public interface ConsultarEntregablesProyectoGradoAsesorInteractor
        extends Interactor<ConsultaCriteriaQuery, PaginatedResult<EntregableProyectoGradoReadModel>> {}
