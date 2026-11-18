package com.arquisoft.entregables.infrastructure.proyectogrado.command.primaryadapter.amqp.proyectos.proyectogrado;

import java.time.Instant;

public record ProyectoGradoEstadoCambiadoPayload(
        String idEvento,
        Instant ocurridoEn,
        String proyectoGradoId,
        String estadoNuevo
) {
}
