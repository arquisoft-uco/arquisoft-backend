package com.arquisoft.artefactos.infrastructure.versionartefacto.query.secondaryadapter.repository;

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
        SELECT va.id                AS id,
               va.artefacto_id      AS artefacto_id,
               va.version_artefacto AS version,
               va.fecha_creacion    AS fecha_creacion,
               va.uri_archivo       AS uri_archivo,
               ea.estudiante_id         AS estudiante_id,
               pg.id                  AS proyecto_grado_id,
               pg.nombre              AS nombre_proyecto,
               vra.nombre_documento   AS nombre_documento,
               vra.version_documento  AS version_documento,
               vra.nombre_repositorio AS nombre_repositorio,
               ase.nombre             AS nombre_asesor,
               ra.id                  AS revision_asesor_id,
               ra.estado_revision_asesor_id AS estado_revision_id,
               er.nombre              AS nombre_estado_revision,
               er.orden               AS orden_estado_revision
        FROM version_artefacto va
                 JOIN artefacto a ON a.id = va.artefacto_id
                 JOIN estudiante_artefacto ea ON ea.artefacto_id = a.id
                 JOIN proyecto_grado pg ON pg.id = a.proyecto_grado_id
                 JOIN version_repositorio_artefacto vra ON vra.id = a.version_repositorio_artefacto_id
                 JOIN asesor ase ON ase.id = a.asesor_id
                 LEFT JOIN revision_asesor ra ON ra.id = (
                         SELECT r.id FROM revision_asesor r
                         WHERE r.version_artefacto_id = va.id
                         ORDER BY r.fecha_creacion DESC, r.id DESC
                         FETCH FIRST 1 ROW ONLY)
                 LEFT JOIN estado_revision_asesor er ON er.id = ra.estado_revision_asesor_id
        """)
@Synchronize({
        "version_artefacto", "artefacto", "estudiante_artefacto", "proyecto_grado",
        "version_repositorio_artefacto", "asesor",
        "revision_asesor", "estado_revision_asesor"})
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VersionArtefactoEstudianteJpaQueryEntity {

    @Id
    @Column(name = "id", columnDefinition = "uuid")
    private UUID id;

    @Column(name = "artefacto_id", columnDefinition = "uuid")
    private UUID artefactoId;

    @Column(name = "version")
    private int version;

    @Column(name = "fecha_creacion")
    private Instant fechaCreacion;

    @Column(name = "uri_archivo")
    private String uriArchivo;

    @Column(name = "estudiante_id", columnDefinition = "uuid")
    private UUID estudianteId;

    @Column(name = "proyecto_grado_id", columnDefinition = "uuid")
    private UUID proyectoGradoId;

    @Column(name = "nombre_proyecto")
    private String nombreProyecto;

    @Column(name = "nombre_documento")
    private String nombreDocumento;

    @Column(name = "version_documento")
    private int versionDocumento;

    @Column(name = "nombre_repositorio")
    private String nombreRepositorio;

    @Column(name = "nombre_asesor")
    private String nombreAsesor;

    @Column(name = "revision_asesor_id", columnDefinition = "uuid")
    private UUID revisionAsesorId;

    @Column(name = "estado_revision_id")
    private String estadoRevisionId;

    @Column(name = "nombre_estado_revision")
    private String nombreEstadoRevision;

    @Column(name = "orden_estado_revision")
    private Integer ordenEstadoRevision;
}
