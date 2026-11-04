package com.arquisoft.usuarios.application.representantecomite.query.secondaryport;

import com.arquisoft.usuarios.application.representantecomite.query.criteria.RepresentanteComiteVigenteCriteria;
import com.arquisoft.usuarios.application.representantecomite.query.readmodel.RepresentanteComiteVigenteReadModel;
import com.arquisoft.shared.query.pagination.PaginatedResult;

public interface RepresentanteComiteQueryOutputPort {

    PaginatedResult<RepresentanteComiteVigenteReadModel> consultarVigentes(RepresentanteComiteVigenteCriteria criteria);
}
