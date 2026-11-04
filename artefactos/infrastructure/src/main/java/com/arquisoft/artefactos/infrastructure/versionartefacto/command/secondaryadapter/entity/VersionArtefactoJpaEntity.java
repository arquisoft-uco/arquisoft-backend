package com.arquisoft.artefactos.infrastructure.versionartefacto.command.secondaryadapter.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "version_artefacto")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VersionArtefactoJpaEntity {

    @Id
    @Column(name = "id", columnDefinition = "UUID")
    private UUID id;

    @Column(name = "artefacto_id", nullable = false, columnDefinition = "UUID")
    private UUID artefacto;

    @Column(name = "version_artefacto", nullable = false)
    private int numeroVersion;

    @Column(name = "fecha_creacion", nullable = false)
    private Instant fechaCreacion;

    @Column(name = "uri_archivo", nullable = false, length = 2000)
    private String uriArchivo;
}
