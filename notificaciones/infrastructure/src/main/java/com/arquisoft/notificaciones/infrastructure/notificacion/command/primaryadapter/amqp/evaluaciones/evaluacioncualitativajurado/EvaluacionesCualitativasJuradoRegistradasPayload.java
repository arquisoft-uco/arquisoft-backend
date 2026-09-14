package com.arquisoft.notificaciones.infrastructure.notificacion.command.primaryadapter.amqp.evaluaciones.evaluacioncualitativajurado;

import java.time.Instant;
import java.util.List;

public record EvaluacionesCualitativasJuradoRegistradasPayload(
        String idEvento,
        Instant ocurridoEn,
        String evaluacionJuradoId,
        String evaluacionId,
        String entregableId,
        String juradoId,
        String proyecto,
        Integer versionEntregable,
        Integer cantidad,
        List<ContactoPayload> estudiantes) {

    public record ContactoPayload(String estudiante, String email) {
    }
}
