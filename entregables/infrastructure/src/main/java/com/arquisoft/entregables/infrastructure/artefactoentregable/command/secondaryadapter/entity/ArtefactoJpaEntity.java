package com.arquisoft.entregables.infrastructure.artefactoentregable.command.secondaryadapter.entity;

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
@Table(name = "artefacto")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ArtefactoJpaEntity {

    @Id
    @Column(name = "id", columnDefinition = "UUID")
    private UUID id;

    @Column(name = "uri_archivo", nullable = false, length = 2000)
    private String uriArchivo;

    @Column(name = "id_artefacto", nullable = false, columnDefinition = "UUID")
    private UUID artefacto;

    @Column(name = "id_repositorio_artefacto", nullable = false, columnDefinition = "UUID")
    private UUID versionRepositorioArtefacto;

    @Column(name = "nombre_documento", nullable = false, length = 100)
    private String nombreDocumento;

    @Column(name = "id_repositorio", nullable = false, columnDefinition = "UUID")
    private UUID repositorio;

    @Column(name = "nombre_repositorio", nullable = false, length = 100)
    private String nombreRepositorio;
}
