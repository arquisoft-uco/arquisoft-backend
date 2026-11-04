package com.arquisoft.evaluaciones.application.evaluacion.query.finder.impl;

import com.arquisoft.evaluaciones.application.evaluacion.query.finder.EvaluacionExisteQueryFinder;
import com.arquisoft.evaluaciones.application.evaluacion.query.secondaryport.EvaluacionAccesoQueryOutputPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class EvaluacionExisteQueryFinderImpl implements EvaluacionExisteQueryFinder {

    private final EvaluacionAccesoQueryOutputPort evaluacionAccesoQueryOutputPort;

    @Override
    public Boolean obtener(UUID evaluacion) {
        return evaluacionAccesoQueryOutputPort.existePorId(evaluacion);
    }
}
