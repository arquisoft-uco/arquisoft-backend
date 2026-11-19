package com.arquisoft.evaluaciones.application.evaluacion.command.secondaryport.entity;

import java.math.BigDecimal;

public record ResumenEvaluacionEntity(
        BigDecimal resumenCualitativo, BigDecimal resumenCuantitativo, BigDecimal notaAcumulada) {
}
