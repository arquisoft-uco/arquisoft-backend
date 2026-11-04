package com.arquisoft.artefactos.application.revisionasesor.query.primaryport.interactor;

import com.arquisoft.artefactos.application.revisionasesor.query.primaryport.model.ConsultarRevisionesAsesorEstudianteQuery;
import com.arquisoft.artefactos.application.revisionasesor.query.readmodel.RevisionAsesorReadModel;
import com.arquisoft.shared.interactor.Interactor;
import com.arquisoft.shared.query.pagination.PaginatedResult;

public interface ConsultarRevisionesAsesorEstudianteInteractor
        extends Interactor<ConsultarRevisionesAsesorEstudianteQuery, PaginatedResult<RevisionAsesorReadModel>> {}
