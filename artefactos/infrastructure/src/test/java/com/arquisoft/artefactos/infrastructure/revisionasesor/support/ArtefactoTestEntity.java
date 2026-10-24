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

    @Column(name = "proyecto_grado_id")
    private UUID proyectoGradoId;

    @Column(name = "version_repositorio_artefacto_id")
    private UUID versionRepositorioArtefactoId;

    @Column(name = "asesor_id")
    private UUID asesorId;

    protected ArtefactoTestEntity() {}

    public ArtefactoTestEntity(UUID id) {
        this(id, null);
    }

    public ArtefactoTestEntity(UUID id, UUID asesorId) {
        this.id = id;
        this.proyectoGradoId = UUID.randomUUID();
        this.versionRepositorioArtefactoId = UUID.randomUUID();
        this.asesorId = asesorId;
    }
}
