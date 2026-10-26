package com.arquisoft.artefactos.application.artefacto.query.secondaryport;

import com.arquisoft.artefactos.application.artefacto.query.criteria.ArtefactoAsesorCriteria;
import com.arquisoft.artefactos.application.artefacto.query.readmodel.ArtefactoReadModel;
import com.arquisoft.shared.query.pagination.PaginatedResult;

public interface ArtefactoQueryOutputPort {

    PaginatedResult<ArtefactoReadModel> consultarTodosAsesor(ArtefactoAsesorCriteria criteria);
}
