package com.arquisoft.fichas.infrastructure.revisionitem.command.secondaryadapter.repository;

import com.arquisoft.fichas.application.revisionitem.command.secondaryport.RevisionItemOutputPort;
import com.arquisoft.fichas.application.revisionitem.command.secondaryport.entity.RevisionItemEntity;
import com.arquisoft.fichas.infrastructure.revisionitem.command.secondaryadapter.mapper.RevisionItemJpaMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class RevisionItemCommandOutputAdapter implements RevisionItemOutputPort {

    private final RevisionItemCommandRepository repository;

    @Override
    public void registrarRevision(RevisionItemEntity revision) {
        repository.save(RevisionItemJpaMapper.toJpaEntity(revision));
    }

    @Override
    public long contarPorItem(UUID itemId) {
        return repository.countByItemId(itemId);
    }
}
