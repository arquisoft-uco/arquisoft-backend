package com.arquisoft.usuarios.application.administrador.query.secondaryport;

import com.arquisoft.usuarios.application.administrador.query.criteria.AdministradorCriteria;
import com.arquisoft.usuarios.application.administrador.query.readmodel.AdministradorReadModel;
import com.arquisoft.shared.query.pagination.PaginatedResult;

public interface AdministradorQueryOutputPort {

    PaginatedResult<AdministradorReadModel> consultarTodos(AdministradorCriteria criteria);
}
