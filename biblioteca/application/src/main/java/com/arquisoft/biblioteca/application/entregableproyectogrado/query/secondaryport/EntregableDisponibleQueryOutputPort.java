package com.arquisoft.biblioteca.application.entregableproyectogrado.query.secondaryport;

import com.arquisoft.biblioteca.application.entregableproyectogrado.query.criteria.EntregableDisponibleCriteria;
import com.arquisoft.biblioteca.application.entregableproyectogrado.query.readmodel.EntregableDisponibleReadModel;
import com.arquisoft.shared.query.pagination.PaginatedResult;

public interface EntregableDisponibleQueryOutputPort {

    PaginatedResult<EntregableDisponibleReadModel> consultar(EntregableDisponibleCriteria criteria);
}
