package com.arquisoft.entregables.application.entregableproyectogrado.query.secondaryport;

import com.arquisoft.entregables.application.entregableproyectogrado.query.criteria.EntregableProyectoGradoCriteria;
import com.arquisoft.entregables.application.entregableproyectogrado.query.readmodel.EntregableProyectoGradoReadModel;
import com.arquisoft.shared.query.pagination.PaginatedResult;

public interface EntregableProyectoGradoQueryOutputPort {

    PaginatedResult<EntregableProyectoGradoReadModel> consultar(EntregableProyectoGradoCriteria criteria);
}
