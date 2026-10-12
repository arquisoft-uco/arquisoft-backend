package com.arquisoft.usuarios.application.asesor.query.usecase;

import com.arquisoft.usuarios.application.asesor.query.criteria.AsesorVigenteCriteria;
import com.arquisoft.usuarios.application.asesor.query.readmodel.AsesorVigenteReadModel;
import com.arquisoft.shared.usecase.UseCase;
import com.arquisoft.shared.query.pagination.PaginatedResult;

public interface ConsultarAsesoresVigentesUseCase
        extends UseCase<AsesorVigenteCriteria, PaginatedResult<AsesorVigenteReadModel>> {}
