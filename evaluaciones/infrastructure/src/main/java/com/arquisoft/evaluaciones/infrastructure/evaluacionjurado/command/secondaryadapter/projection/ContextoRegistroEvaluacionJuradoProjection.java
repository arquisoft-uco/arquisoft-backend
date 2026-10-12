package com.arquisoft.evaluaciones.infrastructure.evaluacionjurado.command.secondaryadapter.projection;

import java.util.UUID;

public interface ContextoRegistroEvaluacionJuradoProjection {

    UUID getId();

    UUID getEvaluacion();

    String getEstado();

    UUID getEntregable();
}
