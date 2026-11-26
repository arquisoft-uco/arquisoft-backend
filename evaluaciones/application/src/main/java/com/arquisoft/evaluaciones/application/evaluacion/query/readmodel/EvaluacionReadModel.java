package com.arquisoft.evaluaciones.application.evaluacion.query.readmodel;

import java.math.BigDecimal;
import java.util.UUID;

public record EvaluacionReadModel(
        UUID id,
        Entregable entregable,
        Estado estado,
        Notas notas,
        Resultado resultado
) {

    public record Entregable(UUID id, String proyecto, Integer version) {}

    public record Estado(String id, String nombre) {}

    public record Notas(BigDecimal notaAsesor, BigDecimal notaJurados, BigDecimal notaFinal) {}

    public record Resultado(String id, String nombre) {}
}
