package com.arquisoft.fichas.infrastructure.observacionitem.command.secondaryadapter.repository;

import com.arquisoft.fichas.application.observacionitem.command.secondaryport.ObservacionItemOutputPort;
import com.arquisoft.fichas.application.observacionitem.command.secondaryport.entity.ContextoObservacionItemEntity;
import com.arquisoft.fichas.application.observacionitem.command.secondaryport.entity.ObservacionItemEntity;
import com.arquisoft.fichas.infrastructure.observacionitem.command.secondaryadapter.mapper.ObservacionItemJpaMapper;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.fichas.ObservacionItemKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ObservacionItemCommandOutputAdapter implements ObservacionItemOutputPort {

    private final ObservacionItemCommandRepository repository;
    private final AppLogger logger;

    @Override
    public void registrarObservacion(ObservacionItemEntity observacion) {
        repository.save(ObservacionItemJpaMapper.toJpaEntity(observacion));
        logger.debug(ObservacionItemKey.LOG_GUARDADA, observacion.id());
    }

    @Override
    public long contarPorRevisionYObservacion(UUID revisionItemId, String observacion) {
        return repository.countByRevisionItemIdAndObservacion(revisionItemId, observacion);
    }

    @Override
    public Optional<ContextoObservacionItemEntity> obtenerContexto(UUID observacionItem) {
        return repository.obtenerContexto(observacionItem);
    }

    @Override
    public long contarOtrasIgualesEnRevision(UUID observacionItem, String observacion) {
        return repository.contarOtrasIgualesEnRevision(observacionItem, observacion);
    }

    @Override
    public void actualizarObservacion(UUID observacionItem, String observacion) {
        repository.actualizarObservacion(observacionItem, observacion);
        logger.debug(ObservacionItemKey.LOG_GUARDADA, observacionItem);
    }
}
