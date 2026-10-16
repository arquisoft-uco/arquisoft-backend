package com.arquisoft.usuarios.application.estudiante.query.secondaryport;

import com.arquisoft.usuarios.application.estudiante.query.criteria.EstudianteVigenteCriteria;
import com.arquisoft.usuarios.application.estudiante.query.readmodel.EstudianteVigenteReadModel;
import com.arquisoft.shared.query.pagination.PaginatedResult;

public interface EstudianteQueryOutputPort {

    PaginatedResult<EstudianteVigenteReadModel> consultarVigentes(EstudianteVigenteCriteria criteria);
}
