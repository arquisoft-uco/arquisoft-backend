package com.arquisoft.evaluaciones.application.evaluacion.command.secondaryport.entity;

import java.math.BigDecimal;
import java.util.UUID;

public record FinalizacionEvaluacionEntity(UUID evaluacion, String estado, BigDecimal notaFinal, String resultado) {
}
