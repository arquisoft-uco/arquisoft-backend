package com.arquisoft.artefactos.infrastructure.versionartefacto.query.secondaryadapter.repository;

import com.arquisoft.shared.jpa.repository.SpecificationQueryRepository;

import java.util.UUID;

public interface VersionArtefactoEstudianteQueryRepository
        extends SpecificationQueryRepository<VersionArtefactoEstudianteJpaQueryEntity, UUID> {

    boolean existsByIdAndEstudianteId(UUID id, UUID estudianteId);
}
