package com.arquisoft.artefactos.infrastructure.revisionasesor.support;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "revision_asesor")
public class RevisionAsesorTestEntity {

    @Id
    @Column(name = "id")
    private UUID id;

    @Column(name = "version_artefacto_id")
    private UUID versionArtefactoId;

    @Column(name = "estado_revision_asesor_id")
    private String estadoRevisionAsesorId;

    @Column(name = "fecha_creacion")
    private Instant fechaCreacion;

    protected RevisionAsesorTestEntity() {}

    public RevisionAsesorTestEntity(UUID id, UUID versionArtefactoId, String estadoRevisionAsesorId) {
        this.id = id;
        this.versionArtefactoId = versionArtefactoId;
        this.estadoRevisionAsesorId = estadoRevisionAsesorId;
        this.fechaCreacion = Instant.parse("2026-10-01T10:00:00Z");
    }
}
