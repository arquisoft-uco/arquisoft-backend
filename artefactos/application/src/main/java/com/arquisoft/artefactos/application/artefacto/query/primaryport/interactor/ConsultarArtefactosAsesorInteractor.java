package com.arquisoft.artefactos.application.artefacto.query.primaryport.interactor;

import com.arquisoft.artefactos.application.artefacto.query.primaryport.model.ConsultarArtefactosAsesorQuery;
import com.arquisoft.artefactos.application.artefacto.query.readmodel.ArtefactoReadModel;
import com.arquisoft.shared.interactor.Interactor;
import com.arquisoft.shared.query.pagination.PaginatedResult;

public interface ConsultarArtefactosAsesorInteractor
        extends Interactor<ConsultarArtefactosAsesorQuery, PaginatedResult<ArtefactoReadModel>> {}
