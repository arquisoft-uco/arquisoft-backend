package com.arquisoft.evaluaciones.infrastructure.evaluacionasesor.command.secondaryadapter.projection;

import java.util.UUID;

public interface ContextoRegistroEvaluacionAsesorProjection {

    UUID getId();

    UUID getEvaluacion();

    String getEstado();

    UUID getEntregable();

    UUID getAsesor();
}
