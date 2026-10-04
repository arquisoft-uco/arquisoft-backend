package com.arquisoft.fichas.application.revisionitem.command.finder.impl;

import com.arquisoft.fichas.application.revisionitem.command.finder.AsesoriaRevisionItemFinder;
import com.arquisoft.fichas.application.revisionitem.command.secondaryport.RevisionItemOutputPort;
import com.arquisoft.fichas.application.revisionitem.command.secondaryport.mapper.AsesoriaRevisionItemMapper;
import com.arquisoft.fichas.domain.revisionitem.model.AsesoriaRevisionItem;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AsesoriaRevisionItemFinderImpl implements AsesoriaRevisionItemFinder {

    private final RevisionItemOutputPort revisionItemOutputPort;

    @Override
    public AsesoriaRevisionItem obtener(UUID revisionItem) {
        return revisionItemOutputPort
                .obtenerAsesoria(revisionItem)
                .map(AsesoriaRevisionItemMapper::toDomain)
                .orElse(AsesoriaRevisionItem.VACIO);
    }
}
