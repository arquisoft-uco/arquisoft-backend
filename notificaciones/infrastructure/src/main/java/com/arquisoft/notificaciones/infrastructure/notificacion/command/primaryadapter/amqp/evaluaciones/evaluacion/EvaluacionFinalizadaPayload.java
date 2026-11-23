package com.arquisoft.notificaciones.infrastructure.notificacion.command.primaryadapter.amqp.evaluaciones.evaluacion;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record EvaluacionFinalizadaPayload(
        String idEvento,
        Instant ocurridoEn,
        String evaluacionId,
        String entregableId,
        String nombreProyecto,
        Integer versionEntregable,
        String resultado,
        String resultadoNombre,
        BigDecimal notaFinal,
        ContactoPayload coordinador,
        List<ContactoPayload> estudiantes,
        ContactoPayload asesor) {

    public record ContactoPayload(String nombre, String email) {
    }
}
