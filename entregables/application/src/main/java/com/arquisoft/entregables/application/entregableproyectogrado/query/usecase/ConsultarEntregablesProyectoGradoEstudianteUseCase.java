package com.arquisoft.entregables.application.entregableproyectogrado.query.usecase;

import com.arquisoft.entregables.application.entregableproyectogrado.query.criteria.EntregableProyectoGradoEstudianteCriteria;
import com.arquisoft.entregables.application.entregableproyectogrado.query.readmodel.EntregableProyectoGradoReadModel;
import com.arquisoft.shared.query.pagination.PaginatedResult;
import com.arquisoft.shared.usecase.UseCase;

public interface ConsultarEntregablesProyectoGradoEstudianteUseCase
        extends UseCase<EntregableProyectoGradoEstudianteCriteria, PaginatedResult<EntregableProyectoGradoReadModel>> {}
