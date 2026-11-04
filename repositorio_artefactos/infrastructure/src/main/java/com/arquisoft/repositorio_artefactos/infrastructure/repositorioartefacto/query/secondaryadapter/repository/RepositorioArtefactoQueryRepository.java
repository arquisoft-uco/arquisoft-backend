package com.arquisoft.repositorio_artefactos.infrastructure.repositorioartefacto.query.secondaryadapter.repository;

import com.arquisoft.shared.jpa.repository.SpecificationQueryRepository;

import java.util.UUID;

public interface RepositorioArtefactoQueryRepository
        extends SpecificationQueryRepository<RepositorioArtefactoJpaQueryEntity, UUID> {
}
