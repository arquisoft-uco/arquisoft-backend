package com.arquisoft.evaluaciones.application.evaluacionjurado.query.secondaryport;

import java.util.Optional;
import java.util.UUID;

public interface EvaluacionJuradoAccesoQueryOutputPort {

    boolean existePorId(UUID evaluacionJurado);

    Optional<String> obtenerProyecto(UUID evaluacionJurado);
}
