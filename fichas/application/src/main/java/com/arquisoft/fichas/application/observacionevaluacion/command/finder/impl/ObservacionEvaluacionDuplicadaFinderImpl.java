package com.arquisoft.fichas.application.observacionevaluacion.command.finder.impl;

import com.arquisoft.fichas.application.observacionevaluacion.command.finder.ObservacionEvaluacionDuplicadaFinder;
import com.arquisoft.fichas.application.observacionevaluacion.command.secondaryport.ObservacionEvaluacionOutputPort;
import com.arquisoft.fichas.domain.observacionevaluacion.AgregacionObservacionEvaluacionDomain;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ObservacionEvaluacionDuplicadaFinderImpl implements ObservacionEvaluacionDuplicadaFinder {

    private final ObservacionEvaluacionOutputPort observacionEvaluacionOutputPort;

    @Override
    public Boolean obtener(AgregacionObservacionEvaluacionDomain agregacion) {
        return observacionEvaluacionOutputPort.existePorEvaluacionYObservacion(
                agregacion.getEvaluacionFichaPerfil(), agregacion.getObservacion());
    }
}
