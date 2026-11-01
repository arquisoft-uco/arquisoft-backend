package com.arquisoft.artefactos.infrastructure.artefacto.query.secondaryadapter.repository;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.Subselect;
import org.hibernate.annotations.Synchronize;

import java.time.Instant;
import java.util.UUID;

@Entity
@Immutable
@Subselect("""
        SELECT a.id                               AS id,
               a.proyecto_grado_id                AS proyecto_grado_id,
               pg.nombre                          AS nombre_proyecto,
               a.version_repositorio_artefacto_id AS version_repositorio_artefacto_id,
               vra.nombre_documento               AS nombre_documento,
               vra.version_documento              AS version_documento,
               vra.nombre_repositorio             AS nombre_repositorio,
               a.asesor_id                        AS asesor_id,
               (SELECT MAX(va.fecha_creacion)
                  FROM version_artefacto va
                 WHERE va.artefacto_id = a.id)    AS fecha_ultima_version,
               (SELECT er.nombre
                  FROM revision_asesor ra
                           JOIN version_artefacto v ON v.id = ra.version_artefacto_id
                           JOIN estado_revision_asesor er ON er.id = ra.estado_revision_asesor_id
                 WHERE v.artefacto_id = a.id
                 ORDER BY ra.fecha_creacion DESC
                 FETCH FIRST 1 ROW ONLY)          AS estado_ultima_revision
        FROM artefacto a
                 JOIN proyecto_grado pg ON pg.id = a.proyecto_grado_id
                 JOIN version_repositorio_artefacto vra ON vra.id = a.version_repositorio_artefacto_id
        WHERE a.omitido_en IS NULL
        """)
@Synchronize({"artefacto", "proyecto_grado", "version_repositorio_artefacto", "version_artefacto",
        "revision_asesor", "estado_revision_asesor"})
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ArtefactoSinAvanceJpaQueryEntity {

    @Id
    @Column(name = "id", columnDefinition = "uuid")
    private UUID id;

    @Column(name = "proyecto_grado_id", columnDefinition = "uuid")
    private UUID proyectoGradoId;

    @Column(name = "nombre_proyecto")
    private String nombreProyecto;

    @Column(name = "version_repositorio_artefacto_id", columnDefinition = "uuid")
    private UUID versionRepositorioArtefactoId;

    @Column(name = "nombre_documento")
    private String nombreDocumento;

    @Column(name = "version_documento")
    private int versionDocumento;

    @Column(name = "nombre_repositorio")
    private String nombreRepositorio;

    @Column(name = "asesor_id", columnDefinition = "uuid")
    private UUID asesorId;

    @Column(name = "fecha_ultima_version")
    private Instant fechaUltimaVersion;

    @Column(name = "estado_ultima_revision")
    private String estadoUltimaRevision;
}
