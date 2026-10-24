package com.arquisoft.repositorio_artefactos.application.repositorioartefacto.query.primaryport.interactor;

import com.arquisoft.repositorio_artefactos.application.repositorioartefacto.query.readmodel.RepositorioArtefactoReadModel;
import com.arquisoft.shared.interactor.Interactor;
import com.arquisoft.shared.query.ConsultaCriteriaQuery;
import com.arquisoft.shared.query.pagination.PaginatedResult;

public interface ConsultarRepositoriosArtefactoInteractor
        extends Interactor<ConsultaCriteriaQuery, PaginatedResult<RepositorioArtefactoReadModel>> {}
