package com.arquisoft.evaluaciones.application.evaluacion.query.readmodel;

import java.util.UUID;

public record EvaluacionReadModel(
        UUID id,
        Entregable entregable,
        Estado estado
) {

    public record Entregable(UUID id, String proyecto, Integer version) {}

    public record Estado(String id, String nombre) {}
}
