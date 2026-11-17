package com.arquisoft.biblioteca.application.entregableproyectogrado.query.usecase;

import com.arquisoft.biblioteca.application.entregableproyectogrado.query.criteria.EntregableDisponibleCriteria;
import com.arquisoft.biblioteca.application.entregableproyectogrado.query.readmodel.EntregableDisponibleReadModel;
import com.arquisoft.shared.query.pagination.PaginatedResult;
import com.arquisoft.shared.usecase.UseCase;

public interface ConsultarEntregablesDisponiblesUseCase
        extends UseCase<EntregableDisponibleCriteria, PaginatedResult<EntregableDisponibleReadModel>> {}
