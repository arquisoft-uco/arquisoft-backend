package com.arquisoft.evaluaciones.infrastructure.evaluacionasesor.query.primaryadapter.web.dto;

import java.util.UUID;

public record EvaluacionAsesorRealizadaResponseDTO(UUID id, Evaluacion evaluacion, Asesor asesor) {

    public record Evaluacion(UUID id, Entregable entregable, Estado estado) {}

    public record Entregable(UUID id, String proyecto, Integer version) {}

    public record Estado(String id, String nombre) {}

    public record Asesor(UUID id, String nombre, String email) {}
}
