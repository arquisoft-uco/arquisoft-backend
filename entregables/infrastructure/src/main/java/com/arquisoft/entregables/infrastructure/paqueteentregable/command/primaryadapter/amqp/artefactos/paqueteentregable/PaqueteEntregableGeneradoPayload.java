package com.arquisoft.entregables.infrastructure.paqueteentregable.command.primaryadapter.amqp.artefactos.paqueteentregable;

import java.time.Instant;

public record PaqueteEntregableGeneradoPayload(
        String idEvento,
        Instant ocurridoEn,
        String paqueteEntregable,
        String entregableProyectoGrado,
        String bucket,
        String claveObjeto,
        String nombreArchivo,
        Long tamanoBytes,
        Instant generadoEn
) {
}
