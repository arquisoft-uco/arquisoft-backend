package com.arquisoft.fichas.application.estadoevaluacionficha.command.finder.impl;

import com.arquisoft.fichas.application.estadoevaluacionficha.command.finder.ObservacionesEvaluacionCountFinder;
import com.arquisoft.fichas.application.observacionevaluacion.command.secondaryport.ObservacionEvaluacionOutputPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ObservacionesEvaluacionCountFinderImpl implements ObservacionesEvaluacionCountFinder {

    private final ObservacionEvaluacionOutputPort observacionEvaluacionOutputPort;

    @Override
    public Long obtener(UUID evaluacionFichaPerfil) {
        return observacionEvaluacionOutputPort.contarPorEvaluacion(evaluacionFichaPerfil);
    }
}
