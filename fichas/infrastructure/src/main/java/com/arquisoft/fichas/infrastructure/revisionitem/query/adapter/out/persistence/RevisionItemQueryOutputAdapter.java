package com.arquisoft.fichas.infrastructure.revisionitem.query.adapter.out.persistence;

import com.arquisoft.fichas.application.revisionitem.query.port.out.RevisionItemQueryOutputPort;
import com.arquisoft.fichas.infrastructure.revisionitem.persistence.RevisionItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class RevisionItemQueryOutputAdapter implements RevisionItemQueryOutputPort {

    private final RevisionItemRepository repository;

    @Override
    public long contarPorItem(UUID itemId) {
        return repository.countByItemId(itemId);
    }
}
