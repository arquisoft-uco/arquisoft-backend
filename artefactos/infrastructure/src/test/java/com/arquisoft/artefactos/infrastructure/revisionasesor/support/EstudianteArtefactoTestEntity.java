package com.arquisoft.artefactos.infrastructure.revisionasesor.support;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.UUID;

@Entity
@Table(name = "estudiante_artefacto")
public class EstudianteArtefactoTestEntity {

    @Id
    @Column(name = "id")
    private UUID id;

    @Column(name = "artefacto_id")
    private UUID artefactoId;

    @Column(name = "estudiante_id")
    private UUID estudianteId;

    protected EstudianteArtefactoTestEntity() {}

    public EstudianteArtefactoTestEntity(UUID id, UUID artefactoId, UUID estudianteId) {
        this.id = id;
        this.artefactoId = artefactoId;
        this.estudianteId = estudianteId;
    }
}
