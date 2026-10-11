package com.arquisoft.evaluaciones.infrastructure.evaluacion.command.secondaryadapter.projection;

import java.util.UUID;

public interface ContextoAsignacionEvaluacionProjection {

    UUID getEvaluacion();

    String getEstado();
}
