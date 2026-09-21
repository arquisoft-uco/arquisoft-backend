package com.arquisoft.evaluaciones.infrastructure.evaluacionjurado.command.secondaryadapter.repository;

import com.arquisoft.evaluaciones.application.evaluacionjurado.command.secondaryport.EvaluacionJuradoOutputPort;
import com.arquisoft.evaluaciones.application.evaluacionjurado.command.secondaryport.entity.EstadoEvaluacionJuradoEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class EvaluacionJuradoCommandOutputAdapter implements EvaluacionJuradoOutputPort {

    private final EvaluacionJuradoCommandRepository repository;

    @Override
    public EstadoEvaluacionJuradoEntity obtenerEstado(UUID evaluacionJurado, UUID jurado) {
        return repository.obtenerEstado(evaluacionJurado, jurado)
                .map(p -> new EstadoEvaluacionJuradoEntity(p.isPertenece(), p.isFinalizada()))
                .orElse(new EstadoEvaluacionJuradoEntity(false, false));
    }

    @Override
    public boolean estaFinalizada(UUID evaluacionJurado) {
        return repository.estaFinalizada(evaluacionJurado).orElse(false);
    }

    @Override
    public boolean estaFinalizadaPorObservacion(UUID observacion) {
        return repository.estaFinalizadaPorObservacion(observacion).orElse(false);
    }
}
