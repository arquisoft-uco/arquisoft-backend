package com.arquisoft.entregables.application.entregableproyectogrado.query.usecase;

import com.arquisoft.entregables.application.entregableproyectogrado.query.criteria.EntregableProyectoGradoCriteria;
import com.arquisoft.entregables.application.entregableproyectogrado.query.readmodel.EntregableProyectoGradoReadModel;
import com.arquisoft.shared.query.pagination.PaginatedResult;
import com.arquisoft.shared.usecase.UseCase;

public interface ConsultarEntregablesProyectoGradoAsesorUseCase
        extends UseCase<EntregableProyectoGradoCriteria, PaginatedResult<EntregableProyectoGradoReadModel>> {}
