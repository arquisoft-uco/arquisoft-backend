package com.arquisoft.repositorio_artefactos.infrastructure.versionrepositorioartefacto.command.secondaryadapter.repository;

import com.arquisoft.repositorio_artefactos.infrastructure.versionrepositorioartefacto.command.secondaryadapter.entity.VersionRepositorioArtefactoJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface VersionRepositorioArtefactoCommandRepository
        extends JpaRepository<VersionRepositorioArtefactoJpaEntity, UUID> {

    Optional<VersionRepositorioArtefactoJpaEntity>
            findFirstByRepositorioArtefactoAndNombreDocumentoIgnoreCaseOrderByNumeroVersionDesc(
                    UUID repositorioArtefacto, String nombreDocumento);
}
