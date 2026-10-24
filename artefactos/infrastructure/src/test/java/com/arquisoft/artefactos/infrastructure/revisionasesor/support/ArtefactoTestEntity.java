package com.arquisoft.artefactos.infrastructure.revisionasesor.support;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.UUID;

@Entity
@Table(name = "artefacto")
public class ArtefactoTestEntity {

    @Id
    @Column(name = "id")
    private UUID id;

    @Column(name = "asesor_id")
    private UUID asesorId;

    protected ArtefactoTestEntity() {}

    public ArtefactoTestEntity(UUID id) {
        this.id = id;
    }

    public ArtefactoTestEntity(UUID id, UUID asesorId) {
        this.id = id;
        this.asesorId = asesorId;
    }
}
