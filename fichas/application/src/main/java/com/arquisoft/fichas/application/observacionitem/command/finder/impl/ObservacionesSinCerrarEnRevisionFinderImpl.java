package com.arquisoft.fichas.application.observacionitem.command.finder.impl;

import com.arquisoft.fichas.application.observacionitem.command.finder.ObservacionesSinCerrarEnRevisionFinder;
import com.arquisoft.fichas.application.observacionitem.command.secondaryport.ObservacionItemOutputPort;
import com.arquisoft.fichas.domain.estadoobservacionrevision.EstadoObservacionRevision;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ObservacionesSinCerrarEnRevisionFinderImpl implements ObservacionesSinCerrarEnRevisionFinder {

    private final ObservacionItemOutputPort observacionItemOutputPort;

    @Override
    public Long obtener(UUID observacionItem) {
        return observacionItemOutputPort.contarOtrasSinCerrarEnRevision(
                observacionItem, EstadoObservacionRevision.CERRADO.getId());
    }
}
