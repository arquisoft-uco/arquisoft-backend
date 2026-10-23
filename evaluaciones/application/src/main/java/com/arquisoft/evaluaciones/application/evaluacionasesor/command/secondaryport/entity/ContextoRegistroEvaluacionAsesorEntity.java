package com.arquisoft.evaluaciones.application.evaluacionasesor.command.secondaryport.entity;

import java.util.UUID;

public record ContextoRegistroEvaluacionAsesorEntity(
        UUID id,
        UUID evaluacion,
        String estado,
        UUID entregable,
        UUID asesor) {
}
