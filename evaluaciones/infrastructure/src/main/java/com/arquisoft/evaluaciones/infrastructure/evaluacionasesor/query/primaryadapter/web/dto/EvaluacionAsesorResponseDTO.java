package com.arquisoft.evaluaciones.infrastructure.evaluacionasesor.query.primaryadapter.web.dto;

import java.util.UUID;

public record EvaluacionAsesorResponseDTO(UUID id, Asesor asesor) {

    public record Asesor(UUID id, String nombre, String email) {}
}
