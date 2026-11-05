package com.arquisoft.usuarios.application.administrador.query.usecase;

import com.arquisoft.usuarios.application.administrador.query.criteria.AdministradorVigenteCriteria;
import com.arquisoft.usuarios.application.administrador.query.readmodel.AdministradorVigenteReadModel;
import com.arquisoft.shared.usecase.UseCase;
import com.arquisoft.shared.query.pagination.PaginatedResult;

public interface ConsultarAdministradoresVigentesUseCase
        extends UseCase<AdministradorVigenteCriteria, PaginatedResult<AdministradorVigenteReadModel>> {}
