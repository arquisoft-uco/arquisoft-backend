package com.arquisoft.evaluaciones.application.evaluacioncuantitativajurado.query.finder.impl;

import com.arquisoft.evaluaciones.application.evaluacioncuantitativajurado.query.finder.EvaluacionCuantitativaJuradoExisteQueryFinder;
import com.arquisoft.evaluaciones.application.evaluacioncuantitativajurado.query.secondaryport.EvaluacionCuantitativaJuradoQueryOutputPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class EvaluacionCuantitativaJuradoExisteQueryFinderImpl implements EvaluacionCuantitativaJuradoExisteQueryFinder {

    private final EvaluacionCuantitativaJuradoQueryOutputPort evaluacionCuantitativaJuradoQueryOutputPort;

    @Override
    public Boolean obtener(UUID evaluacionCuantitativaJurado) {
        return evaluacionCuantitativaJuradoQueryOutputPort.existePorId(evaluacionCuantitativaJurado);
    }
}
