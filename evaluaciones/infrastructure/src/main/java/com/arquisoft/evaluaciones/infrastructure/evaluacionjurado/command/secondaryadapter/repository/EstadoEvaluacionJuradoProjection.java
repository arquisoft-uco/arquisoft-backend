package com.arquisoft.evaluaciones.infrastructure.evaluacionjurado.command.secondaryadapter.repository;

public interface EstadoEvaluacionJuradoProjection {

    boolean isPertenece();

    boolean isFinalizada();
}
