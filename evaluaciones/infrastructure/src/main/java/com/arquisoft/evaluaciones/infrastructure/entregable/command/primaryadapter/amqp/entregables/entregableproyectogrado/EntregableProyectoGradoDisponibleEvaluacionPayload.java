package com.arquisoft.evaluaciones.infrastructure.entregable.command.primaryadapter.amqp.entregables.entregableproyectogrado;

import java.time.Instant;

public record EntregableProyectoGradoDisponibleEvaluacionPayload(
        String idEvento,
        Instant ocurridoEn,
        String entregableProyectoGrado,
        String proyectoGrado,
        String nombreProyecto,
        Integer version
) {
}
