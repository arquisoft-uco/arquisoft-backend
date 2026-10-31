package com.arquisoft.artefactos.infrastructure.revisionasesor.command.secondaryadapter.repository;

import com.arquisoft.artefactos.infrastructure.revisionasesor.command.secondaryadapter.entity.RevisionAsesorJpaEntity;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.NativeQuery;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface RevisionAsesorCommandRepository extends JpaRepository<RevisionAsesorJpaEntity, UUID> {

    // El estado leido es precondicion del cambio y no puede pasar a CERRADA antes del commit de esa
    // escritura: el cierre por el asesor (HU-116) y el cambio de estado de una observacion por el
    // estudiante (HU-021) deben tomar este mismo bloqueo en su transaccion.
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT r FROM RevisionAsesorJpaEntity r WHERE r.id = :revisionAsesor")
    Optional<RevisionAsesorJpaEntity> buscarBloqueadaPorId(@Param("revisionAsesor") UUID revisionAsesor);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE RevisionAsesorJpaEntity r SET r.estado = :estado WHERE r.id = :revisionAsesor")
    int actualizarEstado(@Param("revisionAsesor") UUID revisionAsesor, @Param("estado") String estado);

    boolean existsByVersionArtefactoAndEstadoNot(UUID versionArtefacto, String estado);

    @NativeQuery("""
            SELECT ra.*
            FROM revision_asesor ra
            JOIN version_artefacto va ON va.id = ra.version_artefacto_id
            WHERE va.artefacto_id = :artefacto
              AND va.version_artefacto = (
                  SELECT MAX(v.version_artefacto) FROM version_artefacto v WHERE v.artefacto_id = :artefacto)
            ORDER BY ra.fecha_creacion DESC
            LIMIT 1
            """)
    Optional<RevisionAsesorJpaEntity> buscarMasRecienteDeUltimaVersion(@Param("artefacto") UUID artefacto);

    @NativeQuery("""
            SELECT EXISTS (
                SELECT 1
                FROM revision_asesor ra
                JOIN version_artefacto va ON va.id = ra.version_artefacto_id
                JOIN version_artefacto posterior ON posterior.artefacto_id = va.artefacto_id
                    AND posterior.version_artefacto > va.version_artefacto
                WHERE ra.id = :revisionAsesor
            )
            """)
    boolean existeVersionPosterior(@Param("revisionAsesor") UUID revisionAsesor);

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

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            SELECT r
            FROM RevisionAsesorJpaEntity r, ObservacionJpaEntity o
            WHERE o.id = :observacion
              AND r.id = o.revisionAsesor
            """)
    Optional<RevisionAsesorJpaEntity> buscarBloqueadaPorObservacion(@Param("observacion") UUID observacion);
}
