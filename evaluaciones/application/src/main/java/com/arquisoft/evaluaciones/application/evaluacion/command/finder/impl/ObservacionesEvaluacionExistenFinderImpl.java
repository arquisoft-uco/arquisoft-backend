package com.arquisoft.evaluaciones.application.evaluacion.command.finder.impl;

import com.arquisoft.evaluaciones.application.evaluacion.command.finder.ObservacionesEvaluacionExistenFinder;
import com.arquisoft.evaluaciones.application.evaluacion.command.secondaryport.EvaluacionOutputPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ObservacionesEvaluacionExistenFinderImpl implements ObservacionesEvaluacionExistenFinder {

    private final EvaluacionOutputPort evaluacionOutputPort;

    @Override
    public Boolean obtener(UUID evaluacion) {
        return evaluacionOutputPort.existenObservaciones(evaluacion);
    }
}
