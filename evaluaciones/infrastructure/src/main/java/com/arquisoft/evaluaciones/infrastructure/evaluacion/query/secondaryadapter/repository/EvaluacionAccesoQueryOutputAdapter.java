package com.arquisoft.evaluaciones.infrastructure.evaluacion.query.secondaryadapter.repository;

import com.arquisoft.evaluaciones.application.evaluacion.query.secondaryport.EvaluacionAccesoQueryOutputPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class EvaluacionAccesoQueryOutputAdapter implements EvaluacionAccesoQueryOutputPort {

    private final EvaluacionAccesoQueryRepository repository;

    @Override
    public boolean existePorId(UUID evaluacion) {
        return repository.existsById(evaluacion);
    }
}
