package com.arquisoft.proyectos.application.estadoasesor.query.usecase;

import com.arquisoft.proyectos.application.estadoasesor.query.criteria.EstadoAsesorCriteria;
import com.arquisoft.proyectos.application.estadoasesor.query.readmodel.EstadoAsesorReadModel;
import com.arquisoft.shared.query.pagination.PaginatedResult;
import com.arquisoft.shared.usecase.UseCase;

public interface ConsultarEstadosPosiblesAsesorUseCase
        extends UseCase<EstadoAsesorCriteria, PaginatedResult<EstadoAsesorReadModel>> {}
