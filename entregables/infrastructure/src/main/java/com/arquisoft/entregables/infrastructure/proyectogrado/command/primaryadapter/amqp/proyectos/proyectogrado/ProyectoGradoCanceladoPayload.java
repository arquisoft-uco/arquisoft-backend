package com.arquisoft.entregables.infrastructure.proyectogrado.command.primaryadapter.amqp.proyectos.proyectogrado;

import java.time.Instant;

public record ProyectoGradoCanceladoPayload(
        String idEvento,
        Instant ocurridoEn,
        String proyectoGradoId
) {
}
