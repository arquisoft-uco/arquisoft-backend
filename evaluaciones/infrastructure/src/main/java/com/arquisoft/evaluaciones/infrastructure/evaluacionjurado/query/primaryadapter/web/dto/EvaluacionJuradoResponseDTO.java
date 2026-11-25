package com.arquisoft.evaluaciones.infrastructure.evaluacionjurado.query.primaryadapter.web.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record EvaluacionJuradoResponseDTO(
        UUID id,
        JuradoDTO jurado,
        NotasDTO notas
) {

    public record JuradoDTO(UUID id, String nombre, String email) {}

    public record NotasDTO(BigDecimal resumenCualitativo, BigDecimal resumenCuantitativo, BigDecimal notaAcumulada) {}
}
