package com.arquisoft.evaluaciones.application.evaluacioncuantitativajurado.query.finder.impl;

import com.arquisoft.evaluaciones.application.evaluacioncuantitativajurado.query.finder.EvaluacionCuantitativaJuradoExisteQueryFinder;
import com.arquisoft.evaluaciones.application.evaluacioncuantitativajurado.query.secondaryport.EvaluacionCuantitativaJuradoAccesoQueryOutputPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class EvaluacionCuantitativaJuradoExisteQueryFinderImpl implements EvaluacionCuantitativaJuradoExisteQueryFinder {

    private final EvaluacionCuantitativaJuradoAccesoQueryOutputPort evaluacionCuantitativaJuradoAccesoQueryOutputPort;

    @Override
    public Boolean obtener(UUID evaluacionCuantitativaJurado) {
        return evaluacionCuantitativaJuradoAccesoQueryOutputPort.existePorId(evaluacionCuantitativaJurado);
    }
}
