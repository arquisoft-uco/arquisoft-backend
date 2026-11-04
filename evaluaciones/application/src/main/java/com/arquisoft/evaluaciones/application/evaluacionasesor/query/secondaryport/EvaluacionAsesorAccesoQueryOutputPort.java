package com.arquisoft.evaluaciones.application.evaluacionasesor.query.secondaryport;

import java.util.Optional;
import java.util.UUID;

public interface EvaluacionAsesorAccesoQueryOutputPort {

    Optional<UUID> obtenerAsesorPorId(UUID evaluacionAsesor);

    Optional<UUID> obtenerPorEvaluacion(UUID evaluacion);
}
