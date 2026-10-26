package com.arquisoft.evaluaciones.infrastructure.evaluacioncuantitativaasesor.command.secondaryadapter.repository;

import com.arquisoft.evaluaciones.application.evaluacioncuantitativaasesor.command.secondaryport.EvaluacionCuantitativaAsesorOutputPort;
import com.arquisoft.evaluaciones.application.evaluacioncuantitativaasesor.command.secondaryport.entity.EvaluacionCuantitativaAsesorEntity;
import com.arquisoft.evaluaciones.infrastructure.evaluacioncuantitativaasesor.command.secondaryadapter.mapper.EvaluacionCuantitativaAsesorJpaMapper;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.evaluaciones.EvaluacionCuantitativaAsesorKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class EvaluacionCuantitativaAsesorCommandOutputAdapter implements EvaluacionCuantitativaAsesorOutputPort {

    private final EvaluacionCuantitativaAsesorCommandRepository repository;
    private final AppLogger logger;

    @Override
    public void registrarTodas(List<EvaluacionCuantitativaAsesorEntity> entidades) {
        var jpaEntidades = entidades.stream()
                .map(EvaluacionCuantitativaAsesorJpaMapper::toJpaEntity)
                .toList();
        repository.saveAll(jpaEntidades);
        logger.debug(EvaluacionCuantitativaAsesorKey.LOG_LOTE_GUARDADO, entidades.size());
    }

    @Override
    public Set<UUID> consultarItemsRegistrados(UUID evaluacionAsesor, Set<UUID> items) {
        return repository.findItemsRegistrados(evaluacionAsesor, items);
    }
}
