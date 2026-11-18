package com.arquisoft.fichas.application.revisionitem.command.finder.impl;

import com.arquisoft.fichas.application.revisionitem.command.finder.PertenenciaRevisionItemFinder;
import com.arquisoft.fichas.application.revisionitem.command.secondaryport.RevisionItemOutputPort;
import com.arquisoft.fichas.application.revisionitem.command.secondaryport.mapper.PertenenciaRevisionItemMapper;
import com.arquisoft.fichas.domain.revisionitem.model.PertenenciaRevisionItem;
import com.arquisoft.fichas.domain.revisionitem.model.RevisionItemDeEstudiante;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PertenenciaRevisionItemFinderImpl implements PertenenciaRevisionItemFinder {

    private final RevisionItemOutputPort revisionItemOutputPort;

    @Override
    public PertenenciaRevisionItem obtener(RevisionItemDeEstudiante entrada) {
        return revisionItemOutputPort
                .obtenerPertenencia(entrada.revisionItem(), entrada.estudiante())
                .map(PertenenciaRevisionItemMapper::toDomain)
                .orElse(PertenenciaRevisionItem.VACIO);
    }
}
