package com.arquisoft.notificaciones.infrastructure.notificacion.command.primaryadapter.amqp.artefactos.revisionasesor;

import java.time.Instant;

public record RevisionAsesorEstadoCambiadoPayload(
        String idEvento,
        Instant ocurridoEn,
        String revisionAsesorId,
        String estado,
        String asesorNombre,
        String asesorEmail,
        String estudianteNombre) {
}
