package com.arquisoft.usuarios.application.asesor.query.secondaryport;

import com.arquisoft.usuarios.application.asesor.query.criteria.AsesorVigenteCriteria;
import com.arquisoft.usuarios.application.asesor.query.readmodel.AsesorVigenteReadModel;
import com.arquisoft.shared.query.pagination.PaginatedResult;

public interface AsesorQueryOutputPort {

    PaginatedResult<AsesorVigenteReadModel> consultarVigentes(AsesorVigenteCriteria criteria);
}
