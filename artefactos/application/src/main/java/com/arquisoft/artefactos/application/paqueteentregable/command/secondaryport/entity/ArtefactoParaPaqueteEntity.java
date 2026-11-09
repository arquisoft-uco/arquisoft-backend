package com.arquisoft.artefactos.application.paqueteentregable.command.secondaryport.entity;

import java.time.Instant;
import java.util.UUID;

public record ArtefactoParaPaqueteEntity(
        UUID artefacto,
        String nombreDocumento,
        UUID versionArtefacto,
        int numeroVersionCerrada,
        String uriArchivo,
        Instant fechaVersion,
        int ultimaVersion
) {
}
