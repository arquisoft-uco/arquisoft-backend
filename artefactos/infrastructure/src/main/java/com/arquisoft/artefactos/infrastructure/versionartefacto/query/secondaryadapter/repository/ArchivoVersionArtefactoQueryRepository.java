package com.arquisoft.artefactos.infrastructure.versionartefacto.query.secondaryadapter.repository;

import com.arquisoft.shared.jpa.repository.QueryRepository;

import java.util.UUID;

public interface ArchivoVersionArtefactoQueryRepository
        extends QueryRepository<ArchivoVersionArtefactoJpaQueryEntity, UUID> {
}
