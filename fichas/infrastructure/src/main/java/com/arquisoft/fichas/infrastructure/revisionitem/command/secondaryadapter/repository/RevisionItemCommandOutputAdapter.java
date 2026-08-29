package com.arquisoft.fichas.infrastructure.revisionitem.command.secondaryadapter.repository;

import com.arquisoft.fichas.application.revisionitem.command.secondaryport.RevisionItemOutputPort;
import com.arquisoft.fichas.application.revisionitem.command.secondaryport.entity.RevisionItemEntity;
import com.arquisoft.fichas.infrastructure.estadorevision.command.secondaryadapter.mapper.EstadoRevisionJpaMapper;
import com.arquisoft.fichas.infrastructure.revisionitem.command.secondaryadapter.mapper.RevisionItemJpaMapper;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.key.fichas.RevisionItemKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

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
    public void actualizarEstado(UUID itemId, String estadoRevision) {
        repository.actualizarEstadoRevision(itemId, EstadoRevisionJpaMapper.toReferencia(estadoRevision));
        logger.debug(Mensajes.obtener(RevisionItemKey.LOG_MODIFICADO), itemId);
    }

    @Override
    public long contarPorItem(UUID itemId) {
        return repository.countByItemId(itemId);
    }
}
