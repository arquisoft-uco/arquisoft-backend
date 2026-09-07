package com.arquisoft.evaluaciones.application.evaluacionjurado.query.finder.impl;

import com.arquisoft.evaluaciones.application.evaluacionjurado.query.finder.EvaluacionJuradoExisteQueryFinder;
import com.arquisoft.evaluaciones.application.evaluacionjurado.query.secondaryport.EvaluacionJuradoAccesoQueryOutputPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class EvaluacionJuradoExisteQueryFinderImpl implements EvaluacionJuradoExisteQueryFinder {

    private final EvaluacionJuradoAccesoQueryOutputPort evaluacionJuradoAccesoQueryOutputPort;

    @Override
    public Boolean obtener(UUID evaluacionJurado) {
        return evaluacionJuradoAccesoQueryOutputPort.existePorId(evaluacionJurado);
    }
}
