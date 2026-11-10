package com.arquisoft.artefactos.application.paqueteentregable.command.secondaryport.entity;

import java.time.Instant;
import java.util.UUID;

public record ArtefactoIncluidoEntity(
        UUID artefacto,
        UUID versionArtefacto,
        String nombreDocumento,
        int numeroVersion,
        int ultimaVersion,
        Instant fechaVersion,
        String uriArchivo
) {
}
