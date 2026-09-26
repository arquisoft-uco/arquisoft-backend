package com.arquisoft.usuarios.application.asesor.query.usecase;

import com.arquisoft.usuarios.application.asesor.query.criteria.AsesorCriteria;
import com.arquisoft.usuarios.application.asesor.query.readmodel.AsesorReadModel;
import com.arquisoft.shared.usecase.UseCase;
import com.arquisoft.shared.query.pagination.PaginatedResult;

public interface ConsultarAsesoresAdministradorUseCase
        extends UseCase<AsesorCriteria, PaginatedResult<AsesorReadModel>> {}
