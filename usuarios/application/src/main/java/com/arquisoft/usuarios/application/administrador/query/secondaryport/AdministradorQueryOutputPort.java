package com.arquisoft.usuarios.application.administrador.query.secondaryport;

import com.arquisoft.usuarios.application.administrador.query.criteria.AdministradorVigenteCriteria;
import com.arquisoft.usuarios.application.administrador.query.readmodel.AdministradorVigenteReadModel;
import com.arquisoft.shared.query.pagination.PaginatedResult;

public interface AdministradorQueryOutputPort {

    PaginatedResult<AdministradorVigenteReadModel> consultarVigentes(AdministradorVigenteCriteria criteria);
}
