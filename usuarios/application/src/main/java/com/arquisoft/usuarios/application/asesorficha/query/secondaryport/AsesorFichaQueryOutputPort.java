package com.arquisoft.usuarios.application.asesorficha.query.secondaryport;

import com.arquisoft.usuarios.application.asesorficha.query.criteria.AsesorFichaVigenteCriteria;
import com.arquisoft.usuarios.application.asesorficha.query.readmodel.AsesorFichaVigenteReadModel;
import com.arquisoft.shared.query.pagination.PaginatedResult;

public interface AsesorFichaQueryOutputPort {

    PaginatedResult<AsesorFichaVigenteReadModel> consultarVigentes(AsesorFichaVigenteCriteria criteria);
}
