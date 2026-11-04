package com.arquisoft.artefactos.infrastructure.revisionasesor.support;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
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

    @Column(name = "fecha_creacion")
    private Instant fechaCreacion;

    @Column(name = "uri_archivo")
    private String uriArchivo;

    protected VersionArtefactoTestEntity() {}

    public VersionArtefactoTestEntity(UUID id, UUID artefactoId, int version) {
        this(id, artefactoId, version, Instant.parse("2026-10-01T10:00:00Z"), "artefactos/" + id);
    }

    public VersionArtefactoTestEntity(
            UUID id, UUID artefactoId, int version, Instant fechaCreacion, String uriArchivo) {
        this.id = id;
        this.artefactoId = artefactoId;
        this.version = version;
        this.fechaCreacion = fechaCreacion;
        this.uriArchivo = uriArchivo;
    }
}
