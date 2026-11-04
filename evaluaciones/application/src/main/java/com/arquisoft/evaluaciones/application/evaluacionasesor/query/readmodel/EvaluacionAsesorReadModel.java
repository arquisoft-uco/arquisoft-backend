package com.arquisoft.evaluaciones.application.evaluacionasesor.query.readmodel;

import java.util.UUID;

public record EvaluacionAsesorReadModel(UUID id, Asesor asesor) {

    public record Asesor(UUID id, String nombre, String email) {}
}
