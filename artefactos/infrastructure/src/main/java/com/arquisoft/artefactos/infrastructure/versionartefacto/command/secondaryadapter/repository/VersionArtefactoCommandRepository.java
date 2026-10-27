package com.arquisoft.artefactos.infrastructure.versionartefacto.command.secondaryadapter.repository;

import com.arquisoft.artefactos.infrastructure.versionartefacto.command.secondaryadapter.entity.VersionArtefactoJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface VersionArtefactoCommandRepository extends JpaRepository<VersionArtefactoJpaEntity, UUID> {

    @Query("""
            select coalesce(max(v.numeroVersion), 0)
            from VersionArtefactoJpaEntity v
            where v.artefacto = :artefacto
            """)
    int obtenerUltimoNumero(@Param("artefacto") UUID artefacto);
}
