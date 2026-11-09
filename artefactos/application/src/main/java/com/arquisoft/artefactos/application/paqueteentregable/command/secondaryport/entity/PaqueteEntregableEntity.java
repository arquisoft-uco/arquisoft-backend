package com.arquisoft.artefactos.application.paqueteentregable.command.secondaryport.entity;

import java.time.Instant;
import java.util.UUID;

public record PaqueteEntregableEntity(
        UUID id,
        UUID entregableProyectoGrado,
        UUID proyectoGrado,
        int versionEntregable,
        String claveObjeto,
        long tamanoBytes,
        Instant generadoEn
) {
}
