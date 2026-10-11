package com.arquisoft.evaluaciones.infrastructure.evaluacionjurado.command.secondaryadapter.repository;

import com.arquisoft.evaluaciones.application.evaluacionjurado.command.secondaryport.EvaluacionJuradoOutputPort;
import com.arquisoft.evaluaciones.application.evaluacionjurado.command.secondaryport.entity.EstadoEvaluacionJuradoEntity;
import com.arquisoft.evaluaciones.infrastructure.evaluacionjurado.command.secondaryadapter.mapper.EstadoEvaluacionJuradoJpaMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class EvaluacionJuradoCommandOutputAdapter implements EvaluacionJuradoOutputPort {

    private final EvaluacionJuradoCommandRepository repository;

    @Override
    public Optional<EstadoEvaluacionJuradoEntity> obtenerEstadoBloqueado(UUID evaluacionJurado) {
        return repository.buscarEstadoBloqueado(evaluacionJurado)
                .map(EstadoEvaluacionJuradoJpaMapper::toEntity);
    }
}
