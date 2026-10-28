package com.arquisoft.evaluaciones.application.evaluacionjurado.query.readmodel;

import java.util.UUID;

public record EvaluacionAsignadaJuradoReadModel(
        UUID id,
        Estado estado,
        Evaluacion evaluacion,
        Entregable entregable
) {

    public record Evaluacion(UUID id, Estado estado) {}

    public record Estado(String id, String nombre) {}

    public record Entregable(UUID id, String proyecto, Integer version) {}
}
