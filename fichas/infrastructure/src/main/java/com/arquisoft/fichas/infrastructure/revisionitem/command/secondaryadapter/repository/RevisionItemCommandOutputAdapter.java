package com.arquisoft.fichas.infrastructure.revisionitem.command.secondaryadapter.repository;

import com.arquisoft.fichas.application.revisionitem.command.secondaryport.RevisionItemOutputPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class RevisionItemCommandOutputAdapter implements RevisionItemOutputPort {

    private final RevisionItemCommandRepository repository;

    @Override
    public long contarPorItem(UUID itemId) {
        return repository.countByItemId(itemId);
    }
}
