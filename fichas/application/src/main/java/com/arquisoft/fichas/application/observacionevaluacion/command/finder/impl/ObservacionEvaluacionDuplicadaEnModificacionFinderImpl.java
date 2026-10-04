package com.arquisoft.fichas.application.observacionevaluacion.command.finder.impl;

import com.arquisoft.fichas.application.observacionevaluacion.command.finder.ObservacionEvaluacionDuplicadaEnModificacionFinder;
import com.arquisoft.fichas.application.observacionevaluacion.command.secondaryport.ObservacionEvaluacionOutputPort;
import com.arquisoft.fichas.domain.observacionevaluacion.ModificacionObservacionEvaluacionDomain;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ObservacionEvaluacionDuplicadaEnModificacionFinderImpl
        implements ObservacionEvaluacionDuplicadaEnModificacionFinder {

    private final ObservacionEvaluacionOutputPort observacionEvaluacionOutputPort;

    @Override
    public Boolean obtener(ModificacionObservacionEvaluacionDomain modificacion) {
        return observacionEvaluacionOutputPort.existeOtraConMismoTexto(
                modificacion.getObservacionEvaluacion(), modificacion.getObservacion());
    }
}
