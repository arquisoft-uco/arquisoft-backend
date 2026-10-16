package com.arquisoft.repositorio_artefactos.infrastructure.versionrepositorioartefacto.command.secondaryadapter.repository;

import com.arquisoft.repositorio_artefactos.infrastructure.versionrepositorioartefacto.command.secondaryadapter.entity.VersionRepositorioArtefactoJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface VersionRepositorioArtefactoCommandRepository
        extends JpaRepository<VersionRepositorioArtefactoJpaEntity, UUID> {

    @Query("""
            select coalesce(max(v.numeroVersion), 0)
            from VersionRepositorioArtefactoJpaEntity v
            where v.repositorioArtefacto = :repositorio and v.nombreDocumento = :nombreDocumento
            """)
    int obtenerUltimoNumeroVersion(
            @Param("repositorio") UUID repositorio, @Param("nombreDocumento") String nombreDocumento);
}
