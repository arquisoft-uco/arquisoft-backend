package com.arquisoft.usuarios.application.representantecomite.query.usecase;

import com.arquisoft.usuarios.application.representantecomite.query.criteria.RepresentanteComiteVigenteCriteria;
import com.arquisoft.usuarios.application.representantecomite.query.readmodel.RepresentanteComiteVigenteReadModel;
import com.arquisoft.shared.usecase.UseCase;
import com.arquisoft.shared.query.pagination.PaginatedResult;

public interface ConsultarRepresentantesComiteVigentesUseCase
        extends UseCase<RepresentanteComiteVigenteCriteria, PaginatedResult<RepresentanteComiteVigenteReadModel>> {}
