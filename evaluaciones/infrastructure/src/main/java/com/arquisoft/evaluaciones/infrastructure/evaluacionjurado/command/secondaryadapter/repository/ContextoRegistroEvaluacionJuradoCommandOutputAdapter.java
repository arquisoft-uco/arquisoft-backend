package com.arquisoft.evaluaciones.infrastructure.evaluacionjurado.command.secondaryadapter.repository;

import com.arquisoft.evaluaciones.application.evaluacionjurado.command.secondaryport.ContextoRegistroEvaluacionJuradoOutputPort;
import com.arquisoft.evaluaciones.application.evaluacionjurado.command.secondaryport.entity.ContextoRegistroEvaluacionJuradoEntity;
import com.arquisoft.evaluaciones.infrastructure.evaluacionjurado.command.secondaryadapter.mapper.ContextoRegistroEvaluacionJuradoJpaMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ContextoRegistroEvaluacionJuradoCommandOutputAdapter
        implements ContextoRegistroEvaluacionJuradoOutputPort {

    private final EvaluacionJuradoCommandRepository repository;

    @Override
    public Optional<ContextoRegistroEvaluacionJuradoEntity> obtenerContextoBloqueado(UUID evaluacionJurado) {
        return repository.buscarContextoBloqueado(evaluacionJurado)
                .map(ContextoRegistroEvaluacionJuradoJpaMapper::toEntity);
    }
}
