package com.arquisoft.repositorio_artefactos.infrastructure.versionrepositorioartefacto.command.secondaryadapter.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(
        name = "version_repositorio_artefacto",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_vra_documento_version_repo",
                columnNames = {"nombre_documento", "numero_version", "repositorio_artefacto_id"}))
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VersionRepositorioArtefactoJpaEntity {

    @Id
    @Column(name = "id", columnDefinition = "UUID")
    private UUID id;

    @Column(name = "nombre_documento", nullable = false, length = 100)
    private String nombreDocumento;

    @Column(name = "numero_version", nullable = false)
    private int numeroVersion;

    @Column(name = "fecha_creacion", nullable = false)
    private Instant fechaCreacion;

    @Column(name = "repositorio_artefacto_id", nullable = false, columnDefinition = "UUID")
    private UUID repositorioArtefacto;

    @Column(name = "uri_archivo", nullable = false, length = 2000)
    private String uriArchivo;

    @Column(name = "tipo_contenido", nullable = false, length = 100)
    private String tipoContenido;

    @Column(name = "tamano_bytes", nullable = false)
    private long tamanoBytes;

    @Column(name = "nombre_archivo", nullable = false, length = 255)
    private String nombreArchivo;

    @Column(name = "hash_sha256", length = 64)
    private String hashSha256;
}
