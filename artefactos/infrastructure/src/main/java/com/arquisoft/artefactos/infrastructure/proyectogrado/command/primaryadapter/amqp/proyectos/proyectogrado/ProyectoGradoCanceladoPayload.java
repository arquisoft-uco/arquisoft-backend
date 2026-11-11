package com.arquisoft.artefactos.infrastructure.proyectogrado.command.primaryadapter.amqp.proyectos.proyectogrado;

import java.time.Instant;

public record ProyectoGradoCanceladoPayload(
        String idEvento,
        Instant ocurridoEn,
        String proyectoGradoId
) {
}
