package com.arquisoft.evaluaciones.infrastructure.evaluacionasesor.query.primaryadapter.web.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record EvaluacionAsesorResponseDTO(UUID id, Asesor asesor, Notas notas) {

    public record Asesor(UUID id, String nombre, String email) {}

    public record Notas(BigDecimal resumenCualitativo, BigDecimal resumenCuantitativo, BigDecimal notaAcumulada) {}
}
