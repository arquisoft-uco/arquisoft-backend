package com.arquisoft.artefactos.application.revisionasesor.query.primaryport.interactor;

import com.arquisoft.artefactos.application.revisionasesor.query.primaryport.model.ConsultarRevisionesAsesorAsesorQuery;
import com.arquisoft.artefactos.application.revisionasesor.query.readmodel.RevisionAsesorReadModel;
import com.arquisoft.shared.interactor.Interactor;
import com.arquisoft.shared.query.pagination.PaginatedResult;

public interface ConsultarRevisionesAsesorAsesorInteractor
        extends Interactor<ConsultarRevisionesAsesorAsesorQuery, PaginatedResult<RevisionAsesorReadModel>> {}
