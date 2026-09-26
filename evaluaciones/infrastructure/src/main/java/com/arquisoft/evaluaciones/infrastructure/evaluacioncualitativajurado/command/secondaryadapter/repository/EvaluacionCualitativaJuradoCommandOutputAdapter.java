package com.arquisoft.evaluaciones.infrastructure.evaluacioncualitativajurado.command.secondaryadapter.repository;

import com.arquisoft.evaluaciones.application.evaluacioncualitativajurado.command.secondaryport.EvaluacionCualitativaJuradoOutputPort;
import com.arquisoft.evaluaciones.application.evaluacioncualitativajurado.command.secondaryport.entity.EvaluacionCualitativaJuradoEntity;
import com.arquisoft.evaluaciones.infrastructure.evaluacioncualitativajurado.command.secondaryadapter.mapper.EvaluacionCualitativaJuradoJpaMapper;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.evaluaciones.EvaluacionCualitativaJuradoKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class EvaluacionCualitativaJuradoCommandOutputAdapter implements EvaluacionCualitativaJuradoOutputPort {

    private final EvaluacionCualitativaJuradoCommandRepository repository;
    private final AppLogger logger;

    @Override
    public void registrarTodas(List<EvaluacionCualitativaJuradoEntity> entidades) {
        var jpaEntidades = entidades.stream()
                .map(EvaluacionCualitativaJuradoJpaMapper::toJpaEntity)
                .toList();
        repository.saveAll(jpaEntidades);
        logger.debug(EvaluacionCualitativaJuradoKey.LOG_LOTE_GUARDADO, entidades.size());
    }

    @Override
    public Set<UUID> consultarItemsRegistrados(UUID evaluacionJurado, Set<UUID> items) {
        return repository.findItemsRegistrados(evaluacionJurado, items);
    }
}
