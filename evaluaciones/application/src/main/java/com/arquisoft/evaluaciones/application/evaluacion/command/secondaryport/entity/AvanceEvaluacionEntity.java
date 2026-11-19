package com.arquisoft.evaluaciones.application.evaluacion.command.secondaryport.entity;

import java.util.UUID;

public record AvanceEvaluacionEntity(
        UUID evaluacion,
        String estado,
        UUID entregable,
        String proyecto,
        int version,
        long juradosVigentes,
        long juradosEvaluados,
        boolean asesorCompleto) {
}
