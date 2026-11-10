package com.arquisoft.artefactos.infrastructure.proyectogrado.command.primaryadapter.amqp.entregables.entregableproyectogrado;

import java.time.Instant;

public record EntregableProyectoGradoCanceladoPayload(
        String idEvento,
        Instant ocurridoEn,
        String entregableProyectoGrado,
        String proyectoGrado,
        Integer version
) {
}
