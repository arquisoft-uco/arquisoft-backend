package com.arquisoft.fichas.application.revisionitem.command.finder.impl;

import com.arquisoft.fichas.application.revisionitem.command.finder.RevisionItemFinder;
import com.arquisoft.fichas.application.revisionitem.command.secondaryport.RevisionItemOutputPort;
import com.arquisoft.fichas.application.revisionitem.command.secondaryport.mapper.RevisionItemMapper;
import com.arquisoft.fichas.domain.revisionitem.RevisionItemDomain;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class RevisionItemFinderImpl implements RevisionItemFinder {

    private final RevisionItemOutputPort revisionItemOutputPort;

    @Override
    public RevisionItemDomain obtener(UUID revisionItem) {
        return revisionItemOutputPort.buscarPorId(revisionItem)
                .map(RevisionItemMapper::toDomain)
                .orElse(RevisionItemDomain.VACIO);
    }
}
