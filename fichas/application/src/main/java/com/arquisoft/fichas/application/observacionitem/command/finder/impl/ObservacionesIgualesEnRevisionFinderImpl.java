package com.arquisoft.fichas.application.observacionitem.command.finder.impl;

import com.arquisoft.fichas.application.observacionitem.command.finder.ObservacionesIgualesEnRevisionFinder;
import com.arquisoft.fichas.application.observacionitem.command.secondaryport.ObservacionItemOutputPort;
import com.arquisoft.fichas.domain.observacionitem.AgregacionObservacionItemDomain;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ObservacionesIgualesEnRevisionFinderImpl implements ObservacionesIgualesEnRevisionFinder {

    private final ObservacionItemOutputPort observacionItemOutputPort;

    @Override
    public Long obtener(AgregacionObservacionItemDomain entrada) {
        return observacionItemOutputPort.contarPorRevisionYObservacion(
                entrada.getRevisionItem(), entrada.getObservacion());
    }
}
