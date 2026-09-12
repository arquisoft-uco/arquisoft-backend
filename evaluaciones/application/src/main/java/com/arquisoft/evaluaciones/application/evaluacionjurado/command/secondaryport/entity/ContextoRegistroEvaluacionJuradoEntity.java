package com.arquisoft.evaluaciones.application.evaluacionjurado.command.secondaryport.entity;

import java.util.UUID;

public record ContextoRegistroEvaluacionJuradoEntity(
        UUID id,
        UUID evaluacion,
        UUID jurado,
        String estado,
        UUID entregable,
        String proyecto,
        Integer versionEntregable) {
}
