package com.arquisoft.entregables.application.paqueteentregable.query.secondaryport.entity;

import java.util.UUID;

public record PaqueteEntregableAccesoEntity(
        UUID id,
        UUID entregableProyectoGrado,
        String bucket,
        String claveObjeto,
        String nombreArchivo
) {
}
