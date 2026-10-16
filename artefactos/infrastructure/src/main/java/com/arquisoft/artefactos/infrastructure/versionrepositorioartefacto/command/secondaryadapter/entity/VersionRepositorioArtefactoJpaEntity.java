package com.arquisoft.artefactos.infrastructure.versionrepositorioartefacto.command.secondaryadapter.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "version_repositorio_artefacto")
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

    @Column(name = "version_documento", nullable = false)
    private int versionDocumento;

    @Column(name = "id_repositorio", nullable = false, columnDefinition = "UUID")
    private UUID repositorioArtefacto;

    @Column(name = "nombre_repositorio", nullable = false, length = 100)
    private String nombreRepositorio;

    @Column(name = "nombre_archivo", nullable = false, length = 255)
    private String nombreArchivo;

    @Column(name = "tipo_contenido", nullable = false, length = 100)
    private String tipoContenido;

    @Column(name = "tamano_bytes", nullable = false)
    private long tamanoBytes;
}
