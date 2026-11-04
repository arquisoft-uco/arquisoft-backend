package com.arquisoft.evaluaciones.application.evaluacionjurado.query.readmodel;

import java.util.UUID;

public record EvaluacionJuradoReadModel(
        UUID id,
        Jurado jurado
) {

    public record Jurado(UUID id, String nombre, String email) {}
}
