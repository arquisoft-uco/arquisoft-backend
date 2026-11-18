package com.arquisoft.evaluaciones.infrastructure.evaluacioncualitativaasesor.command.secondaryadapter.repository;

import com.arquisoft.evaluaciones.application.evaluacioncualitativaasesor.command.secondaryport.EvaluacionCualitativaAsesorOutputPort;
import com.arquisoft.evaluaciones.application.evaluacioncualitativaasesor.command.secondaryport.entity.EvaluacionCualitativaAsesorEntity;
import com.arquisoft.evaluaciones.infrastructure.evaluacioncualitativaasesor.command.secondaryadapter.mapper.EvaluacionCualitativaAsesorJpaMapper;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.evaluaciones.EvaluacionCualitativaAsesorKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
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

    @Override
    public boolean existePorItem(UUID item) {
        return repository.existsByItem(item);
    }

    @Override
    public void eliminarPorItems(UUID evaluacionAsesor, Set<UUID> items) {
        repository.eliminarPorItems(evaluacionAsesor, items);
        logger.debug(EvaluacionCualitativaAsesorKey.LOG_LOTE_ELIMINADO, items.size());
    }

    @Override
    public Optional<EvaluacionCualitativaAsesorEntity> obtenerPorId(UUID id) {
        return repository.findById(id).map(EvaluacionCualitativaAsesorJpaMapper::toEntity);
    }

    @Override
    public void actualizar(EvaluacionCualitativaAsesorEntity entity) {
        repository.save(EvaluacionCualitativaAsesorJpaMapper.toJpaEntity(entity));
        logger.debug(EvaluacionCualitativaAsesorKey.LOG_CRITERIO_ACTUALIZADO, entity.id());
    }
}
