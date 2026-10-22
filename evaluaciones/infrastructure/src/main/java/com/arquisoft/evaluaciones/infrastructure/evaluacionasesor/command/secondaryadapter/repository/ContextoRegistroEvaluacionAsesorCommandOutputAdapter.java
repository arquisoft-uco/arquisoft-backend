package com.arquisoft.evaluaciones.infrastructure.evaluacionasesor.command.secondaryadapter.repository;

import com.arquisoft.evaluaciones.application.evaluacionasesor.command.secondaryport.ContextoRegistroEvaluacionAsesorOutputPort;
import com.arquisoft.evaluaciones.application.evaluacionasesor.command.secondaryport.entity.ContextoRegistroEvaluacionAsesorEntity;
import com.arquisoft.evaluaciones.infrastructure.evaluacionasesor.command.secondaryadapter.mapper.ContextoRegistroEvaluacionAsesorJpaMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ContextoRegistroEvaluacionAsesorCommandOutputAdapter
        implements ContextoRegistroEvaluacionAsesorOutputPort {

    private final EvaluacionAsesorCommandRepository repository;

    @Override
    public Optional<ContextoRegistroEvaluacionAsesorEntity> obtenerContextoBloqueado(UUID evaluacionAsesor) {
        return repository.buscarContextoBloqueado(evaluacionAsesor)
                .map(ContextoRegistroEvaluacionAsesorJpaMapper::toEntity);
    }
}
