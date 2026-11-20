package com.arquisoft.entregables.infrastructure.entregableproyectogrado.command.primaryadapter.amqp.evaluaciones.evaluacion;

import java.time.Instant;

public record EvaluacionFinalizadaPayload(
        String idEvento,
        Instant ocurridoEn,
        String entregableId,
        String resultado) {
}
