package com.arquisoft.artefactos.infrastructure.paqueteentregable.command.secondaryadapter.repository;

import com.arquisoft.artefactos.infrastructure.paqueteentregable.command.secondaryadapter.entity.PaqueteEntregableJpaEntity;
import com.arquisoft.artefactos.infrastructure.paqueteentregable.command.secondaryadapter.projection.ArtefactoParaPaqueteProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.NativeQuery;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface PaqueteEntregableCommandRepository extends JpaRepository<PaqueteEntregableJpaEntity, UUID> {

    boolean existsByEntregableProyectoGrado(UUID entregableProyectoGrado);

    @Query("SELECT p.claveObjeto FROM PaqueteEntregableJpaEntity p "
            + "WHERE p.proyectoGrado = :proyectoGrado AND p.versionEntregable < :versionEntregable")
    List<String> obtenerClavesAnteriores(
            @Param("proyectoGrado") UUID proyectoGrado, @Param("versionEntregable") int versionEntregable);

    @NativeQuery("""
            SELECT a.id AS artefacto,
                   vra.nombre_documento AS "nombreDocumento",
                   v.id AS "versionArtefacto",
                   COALESCE(v.version_artefacto, 0) AS "numeroVersionCerrada",
                   v.uri_archivo AS "uriArchivo",
                   v.fecha_creacion AS "fechaVersion",
                   COALESCE((SELECT MAX(u.version_artefacto) FROM version_artefacto u
                             WHERE u.artefacto_id = a.id), 0) AS "ultimaVersion"
            FROM artefacto a
            JOIN version_repositorio_artefacto vra ON vra.id = a.version_repositorio_artefacto_id
            LEFT JOIN version_artefacto v ON v.artefacto_id = a.id
                 AND v.version_artefacto = (SELECT MAX(c.version_artefacto)
                                            FROM version_artefacto c
                                            JOIN revision_asesor r ON r.version_artefacto_id = c.id
                                            WHERE c.artefacto_id = a.id
                                              AND r.estado_revision_asesor_id = :estadoCerrada)
            WHERE a.proyecto_grado_id = :proyectoGrado AND a.omitido_en IS NULL
            ORDER BY vra.nombre_documento, a.id
            """)
    List<ArtefactoParaPaqueteProjection> obtenerArtefactosParaPaquete(
            @Param("proyectoGrado") UUID proyectoGrado, @Param("estadoCerrada") String estadoCerrada);
}
