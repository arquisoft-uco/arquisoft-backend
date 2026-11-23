package com.arquisoft.notificaciones.infrastructure.notificacion.command.primaryadapter.amqp.evaluaciones.evaluacion;

import java.time.Instant;

public record EvaluacionGeneradaPayload(
        String idEvento,
        Instant ocurridoEn,
        String evaluacionId,
        String entregableId,
        String proyectoGradoId,
        String nombreProyecto,
        Integer versionEntregable,
        ContactoPayload coordinador) {

    public record ContactoPayload(String nombre, String email) {
    }
}
