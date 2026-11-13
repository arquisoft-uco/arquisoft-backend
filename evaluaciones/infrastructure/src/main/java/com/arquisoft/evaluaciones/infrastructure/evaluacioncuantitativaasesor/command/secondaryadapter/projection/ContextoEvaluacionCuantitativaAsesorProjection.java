package com.arquisoft.evaluaciones.infrastructure.evaluacioncuantitativaasesor.command.secondaryadapter.projection;

import java.util.UUID;

public interface ContextoEvaluacionCuantitativaAsesorProjection {

    UUID getId();

    UUID getEvaluacionAsesor();

    UUID getAsesor();

    String getEstado();
}
