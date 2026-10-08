package com.arquisoft.fichas.application.observacionitem.command.finder.impl;

import com.arquisoft.fichas.application.observacionitem.command.finder.ContextoObservacionItemFinder;
import com.arquisoft.fichas.application.observacionitem.command.secondaryport.ObservacionItemOutputPort;
import com.arquisoft.fichas.application.observacionitem.command.secondaryport.mapper.ContextoObservacionItemMapper;
import com.arquisoft.fichas.domain.observacionitem.model.ContextoObservacionItem;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ContextoObservacionItemFinderImpl implements ContextoObservacionItemFinder {

    private final ObservacionItemOutputPort observacionItemOutputPort;

    @Override
    public ContextoObservacionItem obtener(UUID observacionItem) {
        return observacionItemOutputPort.obtenerContexto(observacionItem)
                .map(ContextoObservacionItemMapper::toDomain)
                .orElse(ContextoObservacionItem.VACIO);
    }
}
