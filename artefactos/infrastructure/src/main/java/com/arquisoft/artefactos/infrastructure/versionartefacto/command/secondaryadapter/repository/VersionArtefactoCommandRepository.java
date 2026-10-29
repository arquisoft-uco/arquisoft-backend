package com.arquisoft.artefactos.infrastructure.versionartefacto.command.secondaryadapter.repository;

import com.arquisoft.artefactos.infrastructure.versionartefacto.command.secondaryadapter.entity.VersionArtefactoJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.NativeQuery;
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

    @NativeQuery("""
            SELECT NOT EXISTS (
                SELECT 1
                FROM version_artefacto v
                JOIN version_artefacto mayor ON mayor.artefacto_id = v.artefacto_id
                    AND mayor.version_artefacto > v.version_artefacto
                WHERE v.id = :versionArtefacto
            )
            """)
    boolean esUltimaDeSuArtefacto(@Param("versionArtefacto") UUID versionArtefacto);
}
