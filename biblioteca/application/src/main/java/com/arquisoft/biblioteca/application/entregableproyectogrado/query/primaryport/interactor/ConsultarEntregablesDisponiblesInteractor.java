package com.arquisoft.biblioteca.application.entregableproyectogrado.query.primaryport.interactor;

import com.arquisoft.biblioteca.application.entregableproyectogrado.query.primaryport.model.ConsultarEntregablesDisponiblesQuery;
import com.arquisoft.biblioteca.application.entregableproyectogrado.query.readmodel.EntregableDisponibleReadModel;
import com.arquisoft.shared.interactor.Interactor;
import com.arquisoft.shared.query.pagination.PaginatedResult;

public interface ConsultarEntregablesDisponiblesInteractor
        extends Interactor<ConsultarEntregablesDisponiblesQuery, PaginatedResult<EntregableDisponibleReadModel>> {}
