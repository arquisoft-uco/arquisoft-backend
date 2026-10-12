package com.arquisoft.evaluaciones.infrastructure.evaluacioncuantitativajurado.query.secondaryadapter.repository;

import com.arquisoft.evaluaciones.application.evaluacioncuantitativajurado.query.secondaryport.EvaluacionCuantitativaJuradoAccesoQueryOutputPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class EvaluacionCuantitativaJuradoAccesoQueryOutputAdapter
        implements EvaluacionCuantitativaJuradoAccesoQueryOutputPort {

    private final EvaluacionCuantitativaJuradoAccesoQueryRepository repository;

    @Override
    public boolean existePorId(UUID evaluacionCuantitativaJurado) {
        return repository.existsById(evaluacionCuantitativaJurado);
    }
}
