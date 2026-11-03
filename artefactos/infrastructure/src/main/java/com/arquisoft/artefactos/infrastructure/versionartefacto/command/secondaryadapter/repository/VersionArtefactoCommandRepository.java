package com.arquisoft.artefactos.infrastructure.versionartefacto.command.secondaryadapter.repository;

import com.arquisoft.artefactos.infrastructure.versionartefacto.command.secondaryadapter.entity.VersionArtefactoJpaEntity;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.NativeQuery;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
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

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT v FROM VersionArtefactoJpaEntity v WHERE v.id = :id")
    Optional<VersionArtefactoJpaEntity> bloquearPorId(@Param("id") UUID id);

    @NativeQuery("""
            SELECT EXISTS (
                SELECT 1
                FROM version_artefacto v
                JOIN estudiante_artefacto ea ON ea.artefacto_id = v.artefacto_id
                WHERE v.id = :versionArtefacto AND ea.estudiante_id = :estudiante
            )
            """)
    boolean existeParticipacion(@Param("versionArtefacto") UUID versionArtefacto, @Param("estudiante") UUID estudiante);
}
