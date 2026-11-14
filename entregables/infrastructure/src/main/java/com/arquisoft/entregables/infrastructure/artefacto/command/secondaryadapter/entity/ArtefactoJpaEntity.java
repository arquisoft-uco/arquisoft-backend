package com.arquisoft.entregables.infrastructure.artefacto.command.secondaryadapter.entity;

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
@Table(name = "artefacto")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ArtefactoJpaEntity {

    @Id
    @Column(name = "id", columnDefinition = "UUID")
    private UUID id;

    @Column(name = "proyecto_grado_id", nullable = false, columnDefinition = "UUID")
    private UUID proyectoGrado;

    @Column(name = "id_version_artefacto", columnDefinition = "UUID")
    private UUID versionArtefacto;

    @Column(name = "version_artefacto")
    private Integer numeroVersion;

    @Column(name = "fecha_version")
    private Instant fechaVersion;

    @Column(name = "clave_objeto", length = 2000)
    private String claveObjeto;

    @Column(name = "id_repositorio_artefacto", columnDefinition = "UUID")
    private UUID versionRepositorioArtefacto;

    @Column(name = "nombre_documento", length = 100)
    private String nombreDocumento;

    @Column(name = "id_repositorio", columnDefinition = "UUID")
    private UUID repositorio;

    @Column(name = "nombre_repositorio", length = 100)
    private String nombreRepositorio;

    @Column(name = "omitido_en")
    private Instant omitidoEn;

    @Column(name = "omision_ocurrida_en")
    private Instant omisionOcurridaEn;
}
