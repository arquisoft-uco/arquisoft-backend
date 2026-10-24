package com.arquisoft.repositorio_artefactos.infrastructure.versionrepositorioartefacto.query.secondaryadapter.repository;

import com.arquisoft.shared.jpa.repository.QueryRepository;

import java.util.List;
import java.util.UUID;

public interface VersionRepositorioArtefactoQueryRepository
        extends QueryRepository<VersionRepositorioArtefactoJpaQueryEntity, UUID> {

    List<VersionRepositorioArtefactoJpaQueryEntity>
            findByRepositorioArtefactoOrderByNumeroVersionDescNombreDocumentoAsc(UUID repositorioArtefacto);
}
