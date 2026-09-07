package com.arquisoft.evaluaciones.application.evaluacionjurado.query.secondaryport;

import java.util.UUID;

public interface EvaluacionJuradoAccesoQueryOutputPort {

    boolean existePorId(UUID evaluacionJurado);

    boolean perteneceAlEstudiante(UUID evaluacionJurado, UUID estudiante);
}
