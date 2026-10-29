package com.arquisoft.proyectos.application.estadoasesor.query.secondaryport;

import com.arquisoft.proyectos.application.estadoasesor.query.criteria.EstadoAsesorCriteria;
import com.arquisoft.proyectos.application.estadoasesor.query.readmodel.EstadoAsesorReadModel;
import com.arquisoft.shared.query.pagination.PaginatedResult;

public interface EstadoAsesorQueryOutputPort {

    PaginatedResult<EstadoAsesorReadModel> consultarTodos(EstadoAsesorCriteria criteria);
}
