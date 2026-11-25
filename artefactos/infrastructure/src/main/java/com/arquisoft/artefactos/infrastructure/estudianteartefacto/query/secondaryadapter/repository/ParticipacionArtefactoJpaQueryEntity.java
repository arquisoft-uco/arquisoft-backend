package com.arquisoft.artefactos.infrastructure.estudianteartefacto.query.secondaryadapter.repository;

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

import java.util.UUID;

@Entity
@Immutable
@Subselect("""
        SELECT ea.id                              AS id,
               ea.artefacto_id                    AS artefacto_id,
               ea.estudiante_id                   AS estudiante_id,
               a.proyecto_grado_id                AS proyecto_grado_id,
               pg.nombre                          AS nombre_proyecto,
               a.version_repositorio_artefacto_id AS version_repositorio_artefacto_id,
               vra.nombre_documento               AS nombre_documento,
               vra.version_documento              AS version_documento,
               vra.nombre_repositorio             AS nombre_repositorio,
               a.asesor_id                        AS asesor_id,
               s.nombre                           AS nombre_asesor,
               (a.omitido_en IS NOT NULL)         AS omitido,
               (SELECT er.nombre
                  FROM revision_asesor ra
                           JOIN version_artefacto v ON v.id = ra.version_artefacto_id
                           JOIN estado_revision_asesor er ON er.id = ra.estado_revision_asesor_id
                 WHERE v.artefacto_id = a.id
                 ORDER BY ra.fecha_creacion DESC
                 FETCH FIRST 1 ROW ONLY)          AS estado_ultima_revision,
               (pg.estado_proyecto_id = 'FINALIZADO') AS proyecto_finalizado,
               (pg.estado_proyecto_id = 'CANCELADO') AS proyecto_cancelado
        FROM estudiante_artefacto ea
                 JOIN artefacto a ON a.id = ea.artefacto_id
                 JOIN proyecto_grado pg ON pg.id = a.proyecto_grado_id
                 JOIN version_repositorio_artefacto vra ON vra.id = a.version_repositorio_artefacto_id
                 JOIN asesor s ON s.id = a.asesor_id
        """)
@Synchronize({"estudiante_artefacto", "artefacto", "proyecto_grado", "version_repositorio_artefacto", "asesor",
        "version_artefacto", "revision_asesor", "estado_revision_asesor"})
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ParticipacionArtefactoJpaQueryEntity {

    @Id
    @Column(name = "id", columnDefinition = "uuid")
    private UUID id;

    @Column(name = "artefacto_id", columnDefinition = "uuid")
    private UUID artefactoId;

    @Column(name = "estudiante_id", columnDefinition = "uuid")
    private UUID estudianteId;

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

    @Column(name = "nombre_asesor")
    private String nombreAsesor;

    @Column(name = "omitido")
    private boolean omitido;

    @Column(name = "estado_ultima_revision")
    private String estadoUltimaRevision;

    @Column(name = "proyecto_finalizado")
    private boolean proyectoFinalizado;

    @Column(name = "proyecto_cancelado")
    private boolean proyectoCancelado;
}
