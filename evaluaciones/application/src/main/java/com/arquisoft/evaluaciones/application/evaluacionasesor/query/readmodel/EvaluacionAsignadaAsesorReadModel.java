package com.arquisoft.evaluaciones.application.evaluacionasesor.query.readmodel;

import java.util.UUID;

public record EvaluacionAsignadaAsesorReadModel(
        UUID id,
        Evaluacion evaluacion,
        Entregable entregable,
        boolean tieneEvaluacionesCualitativas,
        boolean tieneEvaluacionesCuantitativas
) {

    public record Evaluacion(UUID id, Estado estado) {}

    public record Estado(String id, String nombre) {}

    public record Entregable(UUID id, String proyecto, Integer version) {}
}
