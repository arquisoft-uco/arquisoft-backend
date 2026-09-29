package com.arquisoft.usuarios.application.asesorficha.query.usecase;

import com.arquisoft.usuarios.application.asesorficha.query.criteria.AsesorFichaVigenteCriteria;
import com.arquisoft.usuarios.application.asesorficha.query.readmodel.AsesorFichaVigenteReadModel;
import com.arquisoft.shared.usecase.UseCase;
import com.arquisoft.shared.query.pagination.PaginatedResult;

public interface ConsultarAsesoresFichaVigentesUseCase
        extends UseCase<AsesorFichaVigenteCriteria, PaginatedResult<AsesorFichaVigenteReadModel>> {}
