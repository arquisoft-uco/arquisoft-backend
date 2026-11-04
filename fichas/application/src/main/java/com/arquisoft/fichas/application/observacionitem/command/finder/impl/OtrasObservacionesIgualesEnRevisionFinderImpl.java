package com.arquisoft.fichas.application.observacionitem.command.finder.impl;

import com.arquisoft.fichas.application.observacionitem.command.finder.OtrasObservacionesIgualesEnRevisionFinder;
import com.arquisoft.fichas.application.observacionitem.command.secondaryport.ObservacionItemOutputPort;
import com.arquisoft.fichas.domain.observacionitem.ModificacionObservacionItemDomain;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OtrasObservacionesIgualesEnRevisionFinderImpl implements OtrasObservacionesIgualesEnRevisionFinder {

    private final ObservacionItemOutputPort observacionItemOutputPort;

    @Override
    public Long obtener(ModificacionObservacionItemDomain entrada) {
        return observacionItemOutputPort.contarOtrasIgualesEnRevision(
                entrada.getObservacionItem(), entrada.getObservacion());
    }
}
