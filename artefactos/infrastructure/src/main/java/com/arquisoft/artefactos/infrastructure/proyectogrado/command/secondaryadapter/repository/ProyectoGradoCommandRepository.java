package com.arquisoft.artefactos.infrastructure.proyectogrado.command.secondaryadapter.repository;

import com.arquisoft.artefactos.infrastructure.proyectogrado.command.secondaryadapter.entity.ProyectoGradoJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.NativeQuery;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public interface ProyectoGradoCommandRepository extends JpaRepository<ProyectoGradoJpaEntity, UUID> {

    @NativeQuery("""
            SELECT p.*
            FROM proyecto_grado p
            JOIN artefacto a ON a.proyecto_grado_id = p.id
            WHERE a.id = :artefacto
            """)
    Optional<ProyectoGradoJpaEntity> obtenerPorArtefacto(@Param("artefacto") UUID artefacto);

    @NativeQuery("""
            SELECT p.*
            FROM proyecto_grado p
            JOIN artefacto a ON a.proyecto_grado_id = p.id
            WHERE a.id = :artefacto
            FOR UPDATE OF p
            """)
    Optional<ProyectoGradoJpaEntity> bloquearPorArtefacto(@Param("artefacto") UUID artefacto);

    @NativeQuery("""
            SELECT p.*
            FROM proyecto_grado p
            JOIN artefacto a ON a.proyecto_grado_id = p.id
            JOIN version_artefacto v ON v.artefacto_id = a.id
            JOIN revision_asesor r ON r.version_artefacto_id = v.id
            WHERE r.id = :revisionAsesor
            """)
    Optional<ProyectoGradoJpaEntity> obtenerPorRevisionAsesor(@Param("revisionAsesor") UUID revisionAsesor);

    @NativeQuery("""
            SELECT p.*
            FROM proyecto_grado p
            JOIN artefacto a ON a.proyecto_grado_id = p.id
            JOIN version_artefacto v ON v.artefacto_id = a.id
            JOIN revision_asesor r ON r.version_artefacto_id = v.id
            JOIN observacion o ON o.revision_asesor_id = r.id
            WHERE o.id = :observacion
            """)
    Optional<ProyectoGradoJpaEntity> obtenerPorObservacion(@Param("observacion") UUID observacion);

    @NativeQuery("""
            SELECT p.*
            FROM proyecto_grado p
            JOIN artefacto a ON a.proyecto_grado_id = p.id
            JOIN version_artefacto v ON v.artefacto_id = a.id
            WHERE v.id = :versionArtefacto
            """)
    Optional<ProyectoGradoJpaEntity> obtenerPorVersionArtefacto(@Param("versionArtefacto") UUID versionArtefacto);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE ProyectoGradoJpaEntity p SET p.entregadoEn = :entregadoEn "
            + "WHERE p.id = :id AND (p.entregadoEn IS NULL OR p.entregadoEn < :entregadoEn)")
    int marcarEntregado(@Param("id") UUID id, @Param("entregadoEn") Instant entregadoEn);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE ProyectoGradoJpaEntity p SET p.entregadoEn = NULL "
            + "WHERE p.id = :id AND p.entregadoEn <= :hasta")
    int limpiarEntregado(@Param("id") UUID id, @Param("hasta") Instant hasta);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE ProyectoGradoJpaEntity p SET p.finalizadoEn = :finalizadoEn "
            + "WHERE p.id = :id AND p.finalizadoEn IS NULL")
    int marcarFinalizado(@Param("id") UUID id, @Param("finalizadoEn") Instant finalizadoEn);
}
