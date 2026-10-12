package com.arquisoft.proyectos.application.proyectogrado.query.secondaryport;

import com.arquisoft.proyectos.application.proyectogrado.query.criteria.ProyectoGradoCriteria;
import com.arquisoft.proyectos.application.proyectogrado.query.readmodel.ProyectoGradoReadModel;
import com.arquisoft.shared.query.pagination.PaginatedResult;

public interface ProyectoGradoQueryOutputPort {

    PaginatedResult<ProyectoGradoReadModel> consultarTodas(ProyectoGradoCriteria criteria);
}
