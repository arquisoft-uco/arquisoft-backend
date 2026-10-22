package com.arquisoft.evaluaciones.infrastructure.evaluacioncualitativaasesor.command.secondaryadapter.repository;

import com.arquisoft.evaluaciones.application.evaluacioncualitativaasesor.command.secondaryport.EvaluacionCualitativaAsesorOutputPort;
import com.arquisoft.evaluaciones.application.evaluacioncualitativaasesor.command.secondaryport.entity.EvaluacionCualitativaAsesorEntity;
import com.arquisoft.evaluaciones.infrastructure.evaluacioncualitativaasesor.command.secondaryadapter.mapper.EvaluacionCualitativaAsesorJpaMapper;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.evaluaciones.EvaluacionCualitativaAsesorKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class EvaluacionCualitativaAsesorCommandOutputAdapter implements EvaluacionCualitativaAsesorOutputPort {

    private final EvaluacionCualitativaAsesorCommandRepository repository;
    private final AppLogger logger;

    @Override
    public void registrarTodas(List<EvaluacionCualitativaAsesorEntity> entidades) {
        var jpaEntidades = entidades.stream()
                .map(EvaluacionCualitativaAsesorJpaMapper::toJpaEntity)
                .toList();
        repository.saveAll(jpaEntidades);
        logger.debug(EvaluacionCualitativaAsesorKey.LOG_LOTE_GUARDADO, entidades.size());
    }

    @Override
    public Set<UUID> consultarItemsRegistrados(UUID evaluacionAsesor, Set<UUID> items) {
        return repository.findItemsRegistrados(evaluacionAsesor, items);
    }
}
