package com.arquisoft.evaluaciones.application.evaluacionasesor.query.readmodel;

import java.math.BigDecimal;
import java.util.UUID;

public record EvaluacionAsesorReadModel(UUID id, Asesor asesor, Notas notas) {

    public record Asesor(UUID id, String nombre, String email) {}

    public record Notas(BigDecimal resumenCualitativo, BigDecimal resumenCuantitativo, BigDecimal notaAcumulada) {}
}
