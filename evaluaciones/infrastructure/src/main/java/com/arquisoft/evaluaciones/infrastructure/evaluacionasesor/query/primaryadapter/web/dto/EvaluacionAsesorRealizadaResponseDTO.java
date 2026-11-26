package com.arquisoft.evaluaciones.infrastructure.evaluacionasesor.query.primaryadapter.web.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record EvaluacionAsesorRealizadaResponseDTO(UUID id, Evaluacion evaluacion, Asesor asesor, Notas notas) {

    public record Evaluacion(UUID id, Entregable entregable, Estado estado) {}

    public record Entregable(UUID id, String proyecto, Integer version) {}

    public record Estado(String id, String nombre) {}

    public record Asesor(UUID id, String nombre, String email) {}

    public record Notas(BigDecimal resumenCualitativo, BigDecimal resumenCuantitativo, BigDecimal notaAcumulada) {}
}
