package com.arquisoft.usuarios.application.estudiante.query.usecase;

import com.arquisoft.usuarios.application.estudiante.query.criteria.EstudianteCriteria;
import com.arquisoft.usuarios.application.estudiante.query.readmodel.EstudianteReadModel;
import com.arquisoft.shared.usecase.UseCase;
import com.arquisoft.shared.query.pagination.PaginatedResult;

public interface ConsultarEstudiantesAdministradorUseCase
        extends UseCase<EstudianteCriteria, PaginatedResult<EstudianteReadModel>> {}
