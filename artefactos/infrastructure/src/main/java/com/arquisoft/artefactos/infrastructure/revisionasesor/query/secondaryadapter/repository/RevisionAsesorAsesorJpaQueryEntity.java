package com.arquisoft.artefactos.infrastructure.revisionasesor.query.secondaryadapter.repository;

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
        SELECT ra.id                        AS id,
               ra.version_artefacto_id      AS version_artefacto_id,
               va.artefacto_id              AS artefacto_id,
               va.version_artefacto         AS version,
               a.asesor_id                  AS asesor_id,
               ra.estado_revision_asesor_id AS estado_revision_asesor_id,
               er.nombre                    AS estado_revision_asesor_nombre,
               ra.fecha_creacion            AS fecha_creacion
        FROM revision_asesor ra
                 JOIN version_artefacto va ON va.id = ra.version_artefacto_id
                 JOIN artefacto a ON a.id = va.artefacto_id
                 JOIN estado_revision_asesor er ON er.id = ra.estado_revision_asesor_id
        """)
@Synchronize({"revision_asesor", "version_artefacto", "artefacto", "estado_revision_asesor"})
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RevisionAsesorAsesorJpaQueryEntity {

    @Id
    @Column(name = "id", columnDefinition = "uuid")
    private UUID id;

    @Column(name = "version_artefacto_id", columnDefinition = "uuid")
    private UUID versionArtefactoId;

    @Column(name = "artefacto_id", columnDefinition = "uuid")
    private UUID artefactoId;

    @Column(name = "version")
    private int version;

    @Column(name = "asesor_id", columnDefinition = "uuid")
    private UUID asesorId;

    @Column(name = "estado_revision_asesor_id")
    private String estadoRevisionAsesorId;

    @Column(name = "estado_revision_asesor_nombre")
    private String estadoRevisionAsesorNombre;

    @Column(name = "fecha_creacion")
    private Instant fechaCreacion;
}
