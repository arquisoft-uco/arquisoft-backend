package com.arquisoft.usuarios.application.coordinador.query.usecase;

import com.arquisoft.usuarios.application.coordinador.query.criteria.CoordinadorCriteria;
import com.arquisoft.usuarios.application.coordinador.query.readmodel.CoordinadorReadModel;
import com.arquisoft.shared.usecase.UseCase;
import com.arquisoft.shared.query.pagination.PaginatedResult;

public interface ConsultarCoordinadoresAdministradorUseCase
        extends UseCase<CoordinadorCriteria, PaginatedResult<CoordinadorReadModel>> {}
