package com.arquisoft.artefactos.infrastructure.revisionasesor.command.secondaryadapter.repository;

import com.arquisoft.artefactos.infrastructure.revisionasesor.command.secondaryadapter.entity.RevisionAsesorJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.NativeQuery;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface RevisionAsesorCommandRepository extends JpaRepository<RevisionAsesorJpaEntity, UUID> {

    @Modifying(clearAutomatically = true)
    @Query("UPDATE RevisionAsesorJpaEntity r SET r.estado = :estado WHERE r.id = :revisionAsesor")
    int actualizarEstado(@Param("revisionAsesor") UUID revisionAsesor, @Param("estado") String estado);

    @NativeQuery("""
            SELECT EXISTS (
                SELECT 1
                FROM revision_asesor ra
                JOIN version_artefacto va ON va.id = ra.version_artefacto_id
                JOIN estudiante_artefacto ea ON ea.artefacto_id = va.artefacto_id
                WHERE ra.id = :revisionAsesor AND ea.estudiante_id = :estudiante
            )
            """)
    boolean existeParticipacion(@Param("revisionAsesor") UUID revisionAsesor, @Param("estudiante") UUID estudiante);
}
