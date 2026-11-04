package com.arquisoft.repositorio_artefactos.application.repositorioartefacto.query.secondaryport;

import com.arquisoft.repositorio_artefactos.application.repositorioartefacto.query.criteria.RepositorioArtefactoCriteria;
import com.arquisoft.repositorio_artefactos.application.repositorioartefacto.query.readmodel.RepositorioArtefactoReadModel;
import com.arquisoft.shared.query.pagination.PaginatedResult;

public interface RepositorioArtefactoQueryOutputPort {

    PaginatedResult<RepositorioArtefactoReadModel> consultarTodos(RepositorioArtefactoCriteria criteria);
}
