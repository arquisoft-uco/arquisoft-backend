package com.arquisoft.fichas.application.observacionitem.command.finder.impl;

import com.arquisoft.fichas.application.observacionitem.command.finder.ObservacionesDeRevisionFinder;
import com.arquisoft.fichas.application.observacionitem.command.secondaryport.ObservacionItemOutputPort;
import com.arquisoft.fichas.application.observacionitem.command.secondaryport.mapper.ObservacionesRevisionItemMapper;
import com.arquisoft.fichas.domain.estadoobservacionrevision.EstadoObservacionRevision;
import com.arquisoft.fichas.domain.revisionitem.model.ObservacionesRevisionItem;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ObservacionesDeRevisionFinderImpl implements ObservacionesDeRevisionFinder {

    private final ObservacionItemOutputPort observacionItemOutputPort;

    @Override
    public ObservacionesRevisionItem obtener(UUID revisionItem) {
        var resumen = observacionItemOutputPort.resumirPorRevision(
                revisionItem, EstadoObservacionRevision.PENDIENTE.getId());
        return ObservacionesRevisionItemMapper.toDomain(revisionItem, resumen);
    }
}
