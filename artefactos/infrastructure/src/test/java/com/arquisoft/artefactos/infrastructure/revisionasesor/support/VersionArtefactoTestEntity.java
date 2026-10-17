package com.arquisoft.artefactos.infrastructure.revisionasesor.support;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.UUID;

@Entity
@Table(name = "version_artefacto")
public class VersionArtefactoTestEntity {

    @Id
    @Column(name = "id")
    private UUID id;

    @Column(name = "artefacto_id")
    private UUID artefactoId;

    @Column(name = "version_artefacto")
    private int version;

    protected VersionArtefactoTestEntity() {}

    public VersionArtefactoTestEntity(UUID id, UUID artefactoId, int version) {
        this.id = id;
        this.artefactoId = artefactoId;
        this.version = version;
    }
}
