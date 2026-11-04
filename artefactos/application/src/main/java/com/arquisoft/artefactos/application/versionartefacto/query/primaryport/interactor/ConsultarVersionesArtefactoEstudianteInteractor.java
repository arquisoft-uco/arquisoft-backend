package com.arquisoft.artefactos.application.versionartefacto.query.primaryport.interactor;

import com.arquisoft.artefactos.application.versionartefacto.query.primaryport.model.ConsultarVersionesArtefactoEstudianteQuery;
import com.arquisoft.artefactos.application.versionartefacto.query.readmodel.VersionArtefactoReadModel;
import com.arquisoft.shared.interactor.Interactor;
import com.arquisoft.shared.query.pagination.PaginatedResult;

public interface ConsultarVersionesArtefactoEstudianteInteractor
        extends Interactor<ConsultarVersionesArtefactoEstudianteQuery, PaginatedResult<VersionArtefactoReadModel>> {}
