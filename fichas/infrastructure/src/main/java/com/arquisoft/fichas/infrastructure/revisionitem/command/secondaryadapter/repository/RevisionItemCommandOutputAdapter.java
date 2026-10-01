package com.arquisoft.fichas.infrastructure.revisionitem.command.secondaryadapter.repository;

import com.arquisoft.fichas.application.revisionitem.command.secondaryport.RevisionItemOutputPort;
import com.arquisoft.fichas.application.revisionitem.command.secondaryport.entity.PertenenciaRevisionItemEntity;
import com.arquisoft.fichas.application.revisionitem.command.secondaryport.entity.RevisionItemEntity;
import com.arquisoft.fichas.infrastructure.revisionitem.command.secondaryadapter.mapper.RevisionItemJpaMapper;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.fichas.RevisionItemKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class RevisionItemCommandOutputAdapter implements RevisionItemOutputPort {

    private final RevisionItemCommandRepository repository;
    private final AppLogger logger;

    @Override
    public void registrarRevision(RevisionItemEntity revision) {
        repository.save(RevisionItemJpaMapper.toJpaEntity(revision));
    }

    @Override
    public long contarPorItem(UUID itemId) {
        return repository.countByItemId(itemId);
    }

    @Override
    public Optional<RevisionItemEntity> buscarPorId(UUID revisionItemId) {
        return repository.findById(revisionItemId).map(RevisionItemJpaMapper::toEntity);
    }

    @Override
    public Optional<PertenenciaRevisionItemEntity> obtenerPertenencia(UUID revisionItem, UUID estudiante) {
        return repository.obtenerPertenencia(revisionItem, estudiante);
    }

    @Override
    public void actualizarEstado(UUID revisionItem, String estadoActual, String estadoNuevo) {
        repository.actualizarEstado(revisionItem, estadoActual, estadoNuevo);
        logger.debug(RevisionItemKey.LOG_ACTUALIZADO, revisionItem, estadoNuevo);
    }
}
