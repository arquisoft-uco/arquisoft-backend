package com.arquisoft.usuarios.application.estudiante.query.usecase;

import com.arquisoft.usuarios.application.estudiante.query.criteria.EstudianteVigenteCriteria;
import com.arquisoft.usuarios.application.estudiante.query.readmodel.EstudianteVigenteReadModel;
import com.arquisoft.shared.usecase.UseCase;
import com.arquisoft.shared.query.pagination.PaginatedResult;

public interface ConsultarEstudiantesVigentesUseCase
        extends UseCase<EstudianteVigenteCriteria, PaginatedResult<EstudianteVigenteReadModel>> {}
