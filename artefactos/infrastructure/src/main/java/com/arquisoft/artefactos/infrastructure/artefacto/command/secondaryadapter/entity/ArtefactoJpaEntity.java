package com.arquisoft.artefactos.infrastructure.artefacto.command.secondaryadapter.entity;

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

    @Column(name = "proyecto_grado_id", nullable = false, columnDefinition = "UUID")
    private UUID proyectoGrado;

    @Column(name = "version_repositorio_artefacto_id", nullable = false, columnDefinition = "UUID")
    private UUID versionRepositorioArtefacto;

    @Column(name = "asesor_id", nullable = false, columnDefinition = "UUID")
    private UUID asesor;
}
