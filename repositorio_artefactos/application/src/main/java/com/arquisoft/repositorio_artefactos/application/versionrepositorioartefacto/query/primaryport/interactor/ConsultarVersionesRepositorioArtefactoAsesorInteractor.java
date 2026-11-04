package com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.query.primaryport.interactor;

import com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.query.readmodel.VersionRepositorioArtefactoAsesorReadModel;
import com.arquisoft.shared.interactor.Interactor;
import com.arquisoft.shared.query.ConsultaCriteriaQuery;
import com.arquisoft.shared.query.pagination.PaginatedResult;

public interface ConsultarVersionesRepositorioArtefactoAsesorInteractor
        extends Interactor<ConsultaCriteriaQuery, PaginatedResult<VersionRepositorioArtefactoAsesorReadModel>> {}
