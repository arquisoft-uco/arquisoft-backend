package com.arquisoft.artefactos.application.artefacto.query.primaryport.interactor;

import com.arquisoft.artefactos.application.artefacto.query.primaryport.model.ConsultarArtefactosSinAvanceQuery;
import com.arquisoft.artefactos.application.artefacto.query.readmodel.ArtefactoSinAvanceReadModel;
import com.arquisoft.shared.interactor.Interactor;
import com.arquisoft.shared.query.pagination.PaginatedResult;

public interface ConsultarArtefactosSinAvanceInteractor
        extends Interactor<ConsultarArtefactosSinAvanceQuery, PaginatedResult<ArtefactoSinAvanceReadModel>> {}
