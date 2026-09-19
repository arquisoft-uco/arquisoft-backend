package com.arquisoft.evaluaciones.application.evaluacionjurado.command.secondaryport.entity;

import java.util.UUID;

public record ContextoRegistroEvaluacionJuradoEntity(
        UUID id,
        UUID evaluacion,
        String estado,
        UUID entregable) {
}
