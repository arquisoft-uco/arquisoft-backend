package com.arquisoft.evaluaciones.infrastructure.evaluacionjurado.query.secondaryadapter.repository;

import com.arquisoft.evaluaciones.application.evaluacionjurado.query.secondaryport.EvaluacionJuradoAccesoQueryOutputPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class EvaluacionJuradoAccesoQueryOutputAdapter implements EvaluacionJuradoAccesoQueryOutputPort {

    private final EvaluacionJuradoAccesoQueryRepository repository;

    @Override
    public boolean existePorId(UUID evaluacionJurado) {
        return repository.existsById(evaluacionJurado);
    }

    @Override
    public boolean perteneceAlEstudiante(UUID evaluacionJurado, UUID estudiante) {
        return repository.existePropiedad(evaluacionJurado, estudiante);
    }
}
