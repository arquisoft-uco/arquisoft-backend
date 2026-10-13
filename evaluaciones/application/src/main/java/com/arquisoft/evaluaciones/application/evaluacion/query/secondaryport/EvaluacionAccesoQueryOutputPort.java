package com.arquisoft.evaluaciones.application.evaluacion.query.secondaryport;

import java.util.UUID;

public interface EvaluacionAccesoQueryOutputPort {

    boolean existePorId(UUID evaluacion);
}
