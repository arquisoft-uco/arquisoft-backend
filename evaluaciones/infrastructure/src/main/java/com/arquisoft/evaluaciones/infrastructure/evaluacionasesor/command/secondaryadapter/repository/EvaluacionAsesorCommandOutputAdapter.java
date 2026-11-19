package com.arquisoft.evaluaciones.infrastructure.evaluacionasesor.command.secondaryadapter.repository;

import com.arquisoft.evaluaciones.application.evaluacionasesor.command.secondaryport.EvaluacionAsesorOutputPort;
import com.arquisoft.evaluaciones.application.evaluacionasesor.command.secondaryport.entity.EvaluacionAsesorEntity;
import com.arquisoft.evaluaciones.infrastructure.evaluacionasesor.command.secondaryadapter.mapper.EvaluacionAsesorJpaMapper;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.evaluaciones.EvaluacionAsesorKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class EvaluacionAsesorCommandOutputAdapter implements EvaluacionAsesorOutputPort {

    private final EvaluacionAsesorCommandRepository repository;
    private final AppLogger logger;

    @Override
    public void registrar(EvaluacionAsesorEntity evaluacionAsesor) {
        repository.save(EvaluacionAsesorJpaMapper.toJpaEntity(evaluacionAsesor));
        logger.debug(EvaluacionAsesorKey.LOG_GUARDADA, evaluacionAsesor.id());
    }

    @Override
    public Optional<EvaluacionAsesorEntity> obtenerPorEvaluacion(UUID evaluacion) {
        return repository.findByEvaluacion(evaluacion).map(EvaluacionAsesorJpaMapper::toEntity);
    }

    @Override
    public boolean existenRegistrosPorEvaluacion(UUID evaluacion) {
        return repository.existenRegistrosPorEvaluacion(evaluacion);
    }

    @Override
    public void actualizarAsesor(UUID evaluacionAsesor, UUID asesor) {
        repository.actualizarAsesor(evaluacionAsesor, asesor);
        logger.debug(EvaluacionAsesorKey.LOG_ASESOR_ACTUALIZADO, evaluacionAsesor, asesor);
    }
}
