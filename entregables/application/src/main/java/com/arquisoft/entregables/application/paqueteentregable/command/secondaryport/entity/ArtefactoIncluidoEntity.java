package com.arquisoft.entregables.application.paqueteentregable.command.secondaryport.entity;

import java.time.Instant;
import java.util.UUID;

public record ArtefactoIncluidoEntity(
        UUID artefacto,
        String nombreDocumento,
        int numeroVersion,
        Instant fechaVersion,
        String claveObjeto
) {
}
