package com.arquisoft.fichas.application.revisionitem.command.finder.impl;

import com.arquisoft.fichas.application.revisionitem.command.finder.RevisionesDelItemFinder;
import com.arquisoft.fichas.application.revisionitem.command.secondaryport.RevisionItemOutputPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class RevisionesDelItemFinderImpl implements RevisionesDelItemFinder {

    private final RevisionItemOutputPort revisionItemOutputPort;

    @Override
    public Long obtener(UUID item) {
        return revisionItemOutputPort.contarPorItem(item);
    }
}
