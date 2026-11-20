package com.arquisoft.evaluaciones.application.evaluacion.command.secondaryport.entity;

import java.math.BigDecimal;
import java.util.UUID;

public record ConsolidacionNotasEvaluacionEntity(
        UUID evaluacion,
        BigDecimal notaAsesor,
        BigDecimal notaJurados,
        BigDecimal pesoAsesor,
        BigDecimal pesoJurados,
        int cantidadJurados) {
}
