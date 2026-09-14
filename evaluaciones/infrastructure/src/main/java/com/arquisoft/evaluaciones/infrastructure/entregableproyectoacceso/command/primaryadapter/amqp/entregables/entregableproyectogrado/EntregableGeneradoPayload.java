package com.arquisoft.evaluaciones.infrastructure.entregableproyectoacceso.command.primaryadapter.amqp.entregables.entregableproyectogrado;

import java.time.Instant;

public record EntregableGeneradoPayload(
        String idEvento,
        Instant ocurridoEn,
        String entregableId,
        String proyectoId,
        Integer versionEntregable
) {
}
