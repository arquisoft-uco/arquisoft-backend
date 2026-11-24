package com.arquisoft.evaluaciones.application.evaluacionjurado.query.readmodel;

import java.math.BigDecimal;
import java.util.UUID;

public record EvaluacionJuradoReadModel(
        UUID id,
        Jurado jurado,
        Notas notas
) {

    public record Jurado(UUID id, String nombre, String email) {}

    public record Notas(BigDecimal resumenCualitativo, BigDecimal resumenCuantitativo, BigDecimal notaAcumulada) {}
}
