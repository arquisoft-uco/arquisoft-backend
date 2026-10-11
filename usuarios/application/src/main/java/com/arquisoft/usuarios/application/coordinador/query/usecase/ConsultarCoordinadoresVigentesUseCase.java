package com.arquisoft.usuarios.application.coordinador.query.usecase;

import com.arquisoft.usuarios.application.coordinador.query.criteria.CoordinadorVigenteCriteria;
import com.arquisoft.usuarios.application.coordinador.query.readmodel.CoordinadorVigenteReadModel;
import com.arquisoft.shared.usecase.UseCase;
import com.arquisoft.shared.query.pagination.PaginatedResult;

public interface ConsultarCoordinadoresVigentesUseCase
        extends UseCase<CoordinadorVigenteCriteria, PaginatedResult<CoordinadorVigenteReadModel>> {}
