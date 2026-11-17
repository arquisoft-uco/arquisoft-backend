package com.arquisoft.entregables.application.paqueteentregable.command.secondaryport.entity;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record ContenidoPaqueteEntity(
        UUID entregableProyectoGrado,
        int versionEntregable,
        String nombreProyecto,
        Instant generadoEn,
        List<ArtefactoIncluidoEntity> incluidos
) {
}
