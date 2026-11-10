package com.arquisoft.artefactos.infrastructure.paqueteentregable.command.primaryadapter.amqp.entregables.entregableproyectogrado;

import java.time.Instant;

public record EntregableProyectoGradoGeneradoPayload(
        String idEvento,
        Instant ocurridoEn,
        String entregableProyectoGrado,
        String proyectoGrado,
        Integer version
) {
}
