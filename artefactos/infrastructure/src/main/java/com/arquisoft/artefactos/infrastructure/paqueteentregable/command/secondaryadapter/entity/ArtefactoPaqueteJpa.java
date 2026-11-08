package com.arquisoft.artefactos.infrastructure.paqueteentregable.command.secondaryadapter.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Embeddable
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ArtefactoPaqueteJpa {

    @Column(name = "artefacto_id", nullable = false, columnDefinition = "UUID")
    private UUID artefacto;

    @Column(name = "numero_version", nullable = false)
    private int numeroVersion;
}
