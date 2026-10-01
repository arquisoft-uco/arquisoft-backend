package com.arquisoft.usuarios.application.representantecomite.query.usecase;

import com.arquisoft.usuarios.application.representantecomite.query.criteria.RepresentanteComiteCriteria;
import com.arquisoft.usuarios.application.representantecomite.query.readmodel.RepresentanteComiteReadModel;
import com.arquisoft.shared.usecase.UseCase;
import com.arquisoft.shared.query.pagination.PaginatedResult;

public interface ConsultarRepresentantesComiteAdministradorUseCase
        extends UseCase<RepresentanteComiteCriteria, PaginatedResult<RepresentanteComiteReadModel>> {}
