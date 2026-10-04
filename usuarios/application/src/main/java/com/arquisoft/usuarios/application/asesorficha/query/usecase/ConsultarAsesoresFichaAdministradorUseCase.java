package com.arquisoft.usuarios.application.asesorficha.query.usecase;

import com.arquisoft.usuarios.application.asesorficha.query.criteria.AsesorFichaCriteria;
import com.arquisoft.usuarios.application.asesorficha.query.readmodel.AsesorFichaReadModel;
import com.arquisoft.shared.usecase.UseCase;
import com.arquisoft.shared.query.pagination.PaginatedResult;

public interface ConsultarAsesoresFichaAdministradorUseCase
        extends UseCase<AsesorFichaCriteria, PaginatedResult<AsesorFichaReadModel>> {}
