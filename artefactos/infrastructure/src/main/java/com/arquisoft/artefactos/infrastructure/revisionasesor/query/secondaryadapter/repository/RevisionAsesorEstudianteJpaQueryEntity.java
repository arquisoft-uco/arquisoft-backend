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

import java.util.UUID;

@Entity
@Immutable
@Subselect("""
        SELECT ra.id                        AS id,
               ra.version_artefacto_id      AS version_artefacto_id,
               va.artefacto_id              AS artefacto_id,
               va.version_artefacto         AS version,
               ea.estudiante_id             AS estudiante_id,
               ra.estado_revision_asesor_id AS estado_revision_asesor_id,
               er.nombre                    AS estado_revision_asesor_nombre
        FROM revision_asesor ra
                 JOIN version_artefacto va ON va.id = ra.version_artefacto_id
                 JOIN estudiante_artefacto ea ON ea.artefacto_id = va.artefacto_id
                 JOIN estado_revision_asesor er ON er.id = ra.estado_revision_asesor_id
        """)
@Synchronize({"revision_asesor", "version_artefacto", "estudiante_artefacto", "estado_revision_asesor"})
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RevisionAsesorEstudianteJpaQueryEntity {

    @Id
    @Column(name = "id", columnDefinition = "uuid")
    private UUID id;

    @Column(name = "version_artefacto_id", columnDefinition = "uuid")
    private UUID versionArtefactoId;

    @Column(name = "artefacto_id", columnDefinition = "uuid")
    private UUID artefactoId;

    @Column(name = "version")
    private int version;

    @Column(name = "estudiante_id", columnDefinition = "uuid")
    private UUID estudianteId;

    @Column(name = "estado_revision_asesor_id")
    private String estadoRevisionAsesorId;

    @Column(name = "estado_revision_asesor_nombre")
    private String estadoRevisionAsesorNombre;
}
