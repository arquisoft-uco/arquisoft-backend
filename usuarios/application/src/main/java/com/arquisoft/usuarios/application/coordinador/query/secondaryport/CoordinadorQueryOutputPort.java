package com.arquisoft.usuarios.application.coordinador.query.secondaryport;

import com.arquisoft.usuarios.application.coordinador.query.criteria.CoordinadorVigenteCriteria;
import com.arquisoft.usuarios.application.coordinador.query.readmodel.CoordinadorVigenteReadModel;
import com.arquisoft.shared.query.pagination.PaginatedResult;

public interface CoordinadorQueryOutputPort {

    PaginatedResult<CoordinadorVigenteReadModel> consultarVigentes(CoordinadorVigenteCriteria criteria);
}
