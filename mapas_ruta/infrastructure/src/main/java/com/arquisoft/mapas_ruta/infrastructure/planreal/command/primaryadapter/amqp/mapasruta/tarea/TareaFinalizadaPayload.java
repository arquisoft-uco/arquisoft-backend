package com.arquisoft.mapas_ruta.infrastructure.planreal.command.primaryadapter.amqp.mapasruta.tarea;

import java.time.Instant;

public record TareaFinalizadaPayload(
        String idEvento,
        Instant ocurridoEn,
        String tarea
) {
}
