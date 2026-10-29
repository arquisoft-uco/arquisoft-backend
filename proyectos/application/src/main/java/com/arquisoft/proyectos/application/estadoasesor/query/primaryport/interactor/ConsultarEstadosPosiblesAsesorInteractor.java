package com.arquisoft.proyectos.application.estadoasesor.query.primaryport.interactor;

import com.arquisoft.proyectos.application.estadoasesor.query.readmodel.EstadoAsesorReadModel;
import com.arquisoft.shared.interactor.Interactor;
import com.arquisoft.shared.query.ConsultaCriteriaQuery;
import com.arquisoft.shared.query.pagination.PaginatedResult;

public interface ConsultarEstadosPosiblesAsesorInteractor
        extends Interactor<ConsultaCriteriaQuery, PaginatedResult<EstadoAsesorReadModel>> {}
