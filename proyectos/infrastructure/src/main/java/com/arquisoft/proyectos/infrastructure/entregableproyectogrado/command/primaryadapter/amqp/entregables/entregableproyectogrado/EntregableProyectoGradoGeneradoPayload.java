package com.arquisoft.proyectos.infrastructure.entregableproyectogrado.command.primaryadapter.amqp.entregables.entregableproyectogrado;

import java.time.Instant;

public record EntregableProyectoGradoGeneradoPayload(
        String idEvento,
        Instant ocurridoEn,
        String entregableProyectoGrado,
        String proyectoGrado,
        Integer version
) {
}
