package com.arquisoft.evaluaciones.application.evaluacion.command.result;

import java.util.UUID;

public sealed interface GeneracionEvaluacionResult {

    record Generada(UUID evaluacion, UUID entregable) implements GeneracionEvaluacionResult {}

    record Duplicada(UUID entregable) implements GeneracionEvaluacionResult {}
}
