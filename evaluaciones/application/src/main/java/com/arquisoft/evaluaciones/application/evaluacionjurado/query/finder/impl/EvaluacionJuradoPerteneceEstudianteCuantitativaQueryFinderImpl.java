package com.arquisoft.evaluaciones.application.evaluacionjurado.query.finder.impl;

import com.arquisoft.evaluaciones.application.evaluacioncuantitativajurado.query.criteria.EvaluacionCuantitativaJuradoCriteria;
import com.arquisoft.evaluaciones.application.evaluacionjurado.query.finder.EvaluacionJuradoPerteneceEstudianteCuantitativaQueryFinder;
import com.arquisoft.evaluaciones.application.evaluacionjurado.query.secondaryport.EvaluacionJuradoAccesoQueryOutputPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EvaluacionJuradoPerteneceEstudianteCuantitativaQueryFinderImpl
        implements EvaluacionJuradoPerteneceEstudianteCuantitativaQueryFinder {

    private final EvaluacionJuradoAccesoQueryOutputPort evaluacionJuradoAccesoQueryOutputPort;

    @Override
    public Boolean obtener(EvaluacionCuantitativaJuradoCriteria criteria) {
        return evaluacionJuradoAccesoQueryOutputPort.perteneceAlEstudiante(
                criteria.evaluacionJuradoId(), criteria.estudianteId());
    }
}
