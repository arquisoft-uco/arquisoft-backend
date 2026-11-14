package com.arquisoft.entregables.application.paqueteentregable.command.secondaryport.entity;

import java.time.Instant;
import java.util.UUID;

public record PaqueteEntregableEntity(
        UUID id,
        UUID entregableProyectoGrado,
        String bucket,
        String claveObjeto,
        String nombreArchivo,
        long tamanoBytes,
        Instant generadoEn
) {
}
