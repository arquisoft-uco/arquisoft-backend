package com.arquisoft.evaluaciones.infrastructure.evaluacionjurado.command.secondaryadapter.projection;

import java.util.UUID;

public interface EstadoEvaluacionJuradoProjection {

    UUID getId();

    UUID getJurado();

    String getEstado();
}
