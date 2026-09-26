package com.arquisoft.usuarios.application.asesor.query.secondaryport;

import com.arquisoft.usuarios.application.asesor.query.criteria.AsesorCriteria;
import com.arquisoft.usuarios.application.asesor.query.criteria.AsesorVigenteCriteria;
import com.arquisoft.usuarios.application.asesor.query.readmodel.AsesorReadModel;
import com.arquisoft.usuarios.application.asesor.query.readmodel.AsesorVigenteReadModel;
import com.arquisoft.shared.query.pagination.PaginatedResult;

public interface AsesorQueryOutputPort {

    PaginatedResult<AsesorReadModel> consultarTodos(AsesorCriteria criteria);

    PaginatedResult<AsesorVigenteReadModel> consultarVigentes(AsesorVigenteCriteria criteria);
}
