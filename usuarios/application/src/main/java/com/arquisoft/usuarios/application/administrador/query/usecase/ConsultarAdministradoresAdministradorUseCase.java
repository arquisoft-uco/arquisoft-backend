package com.arquisoft.usuarios.application.administrador.query.usecase;

import com.arquisoft.usuarios.application.administrador.query.criteria.AdministradorCriteria;
import com.arquisoft.usuarios.application.administrador.query.readmodel.AdministradorReadModel;
import com.arquisoft.shared.usecase.UseCase;
import com.arquisoft.shared.query.pagination.PaginatedResult;

public interface ConsultarAdministradoresAdministradorUseCase
        extends UseCase<AdministradorCriteria, PaginatedResult<AdministradorReadModel>> {}
