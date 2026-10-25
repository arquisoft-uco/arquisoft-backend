package com.arquisoft.artefactos.infrastructure.asesor.command.secondaryadapter.repository;

import com.arquisoft.artefactos.infrastructure.asesor.command.secondaryadapter.entity.AsesorJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.NativeQuery;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface AsesorCommandRepository extends JpaRepository<AsesorJpaEntity, UUID> {

    @NativeQuery("""
            SELECT a.id AS id, a.nombre AS nombre, a.email AS email
            FROM asesor a
            JOIN artefacto ar ON ar.asesor_id = a.id
            JOIN version_artefacto va ON va.artefacto_id = ar.id
            JOIN revision_asesor ra ON ra.version_artefacto_id = va.id
            WHERE ra.id = :revisionAsesor
            """)
    Optional<AsesorJpaEntity> findByRevisionAsesor(@Param("revisionAsesor") UUID revisionAsesor);
}
