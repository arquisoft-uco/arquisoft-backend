package com.arquisoft.artefactos.infrastructure.versionartefacto.command.secondaryadapter.repository;

import com.arquisoft.artefactos.infrastructure.versionartefacto.command.secondaryadapter.entity.VersionArtefactoJpaEntity;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.NativeQuery;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
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

    @NativeQuery("""
            SELECT COALESCE(SUM(v.tamano_bytes), 0)
            FROM artefacto base
            JOIN artefacto a ON a.proyecto_grado_id = base.proyecto_grado_id AND a.omitido_en IS NULL
            JOIN version_artefacto v ON v.artefacto_id = a.id
            WHERE base.id = :artefacto
              AND v.version_artefacto >= COALESCE((
                    SELECT MAX(c.version_artefacto)
                    FROM version_artefacto c
                    JOIN revision_asesor r ON r.version_artefacto_id = c.id
                    WHERE c.artefacto_id = a.id AND r.estado_revision_asesor_id = :estadoCerrada), 0)
            """)
    long tamanoVigenteDelProyectoDeArtefacto(
            @Param("artefacto") UUID artefacto, @Param("estadoCerrada") String estadoCerrada);

    @NativeQuery("""
            SELECT COALESCE(SUM(v.tamano_bytes), 0)
            FROM version_artefacto v
            WHERE v.artefacto_id = :artefacto
              AND v.version_artefacto >= COALESCE((
                    SELECT MAX(c.version_artefacto)
                    FROM version_artefacto c
                    JOIN revision_asesor r ON r.version_artefacto_id = c.id
                    WHERE c.artefacto_id = v.artefacto_id AND r.estado_revision_asesor_id = :estadoCerrada), 0)
            """)
    long tamanoVigenteDelArtefacto(@Param("artefacto") UUID artefacto, @Param("estadoCerrada") String estadoCerrada);

    @NativeQuery("""
            SELECT v.uri_archivo
            FROM version_artefacto v
            JOIN artefacto a ON a.id = v.artefacto_id
            WHERE a.proyecto_grado_id = :proyectoGrado
            ORDER BY v.id
            """)
    List<String> obtenerUrisArchivoPorProyectoGrado(@Param("proyectoGrado") UUID proyectoGrado);
}
