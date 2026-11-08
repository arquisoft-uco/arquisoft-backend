package com.arquisoft.proyectos.infrastructure.entregableproyectogrado.command.primaryadapter.amqp.entregables.entregableproyectogrado;

import java.time.Instant;

public record EntregableProyectoGradoCanceladoPayload(
        String idEvento,
        Instant ocurridoEn,
        String entregableProyectoGrado,
        String proyectoGrado,
        Integer version
) {
}
