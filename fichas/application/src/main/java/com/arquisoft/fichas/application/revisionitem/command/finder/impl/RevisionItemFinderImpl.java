package com.arquisoft.fichas.application.revisionitem.command.finder.impl;

import com.arquisoft.fichas.application.revisionitem.command.finder.RevisionItemFinder;
import com.arquisoft.fichas.application.revisionitem.command.secondaryport.RevisionItemOutputPort;
import com.arquisoft.fichas.application.revisionitem.command.secondaryport.entity.RevisionItemEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class RevisionItemFinderImpl implements RevisionItemFinder {

    private final RevisionItemOutputPort revisionItemOutputPort;

    @Override
    public Optional<RevisionItemEntity> obtener(UUID revisionItem) {
        return revisionItemOutputPort.buscarPorId(revisionItem);
    }
}
