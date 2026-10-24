package com.arquisoft.repositorio_artefactos.application.repositorioartefacto.query.usecase;

import com.arquisoft.repositorio_artefactos.application.repositorioartefacto.query.criteria.RepositorioArtefactoCriteria;
import com.arquisoft.repositorio_artefactos.application.repositorioartefacto.query.readmodel.RepositorioArtefactoReadModel;
import com.arquisoft.shared.query.pagination.PaginatedResult;
import com.arquisoft.shared.usecase.UseCase;

public interface ConsultarRepositoriosArtefactoUseCase
        extends UseCase<RepositorioArtefactoCriteria, PaginatedResult<RepositorioArtefactoReadModel>> {}
