package com.arquisoft.evaluaciones.infrastructure.observacionitemasesor.command.secondaryadapter.repository;

import com.arquisoft.evaluaciones.application.observacionitemasesor.command.secondaryport.ObservacionItemAsesorOutputPort;
import com.arquisoft.evaluaciones.application.observacionitemasesor.command.secondaryport.entity.ObservacionItemAsesorEntity;
import com.arquisoft.evaluaciones.infrastructure.observacionitemasesor.command.secondaryadapter.mapper.ObservacionItemAsesorJpaMapper;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.evaluaciones.ObservacionItemAsesorKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ObservacionItemAsesorCommandOutputAdapter implements ObservacionItemAsesorOutputPort {

    private final ObservacionItemAsesorCommandRepository repository;
    private final AppLogger logger;

    @Override
    public void registrar(ObservacionItemAsesorEntity entity) {
        repository.save(ObservacionItemAsesorJpaMapper.toJpaEntity(entity));
        logger.debug(ObservacionItemAsesorKey.LOG_GUARDADA, entity.id());
    }

    @Override
    public boolean existePorEvaluacionYDescripcion(UUID evaluacionCuantitativaAsesor, String descripcion) {
        return repository.existsByEvaluacionCuantitativaAsesorIdAndDescripcion(
                evaluacionCuantitativaAsesor, descripcion);
    }
}
