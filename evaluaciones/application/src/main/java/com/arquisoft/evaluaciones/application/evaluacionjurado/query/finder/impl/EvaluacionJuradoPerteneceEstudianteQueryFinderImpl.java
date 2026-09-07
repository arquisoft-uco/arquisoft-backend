package com.arquisoft.evaluaciones.application.evaluacionjurado.query.finder.impl;

import com.arquisoft.evaluaciones.application.evaluacioncualitativajurado.query.criteria.EvaluacionCualitativaJuradoCriteria;
import com.arquisoft.evaluaciones.application.evaluacionjurado.query.finder.EvaluacionJuradoPerteneceEstudianteQueryFinder;
import com.arquisoft.evaluaciones.application.evaluacionjurado.query.secondaryport.EvaluacionJuradoAccesoQueryOutputPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EvaluacionJuradoPerteneceEstudianteQueryFinderImpl
        implements EvaluacionJuradoPerteneceEstudianteQueryFinder {

    private final EvaluacionJuradoAccesoQueryOutputPort evaluacionJuradoAccesoQueryOutputPort;

    @Override
    public Boolean obtener(EvaluacionCualitativaJuradoCriteria criteria) {
        return evaluacionJuradoAccesoQueryOutputPort.perteneceAlEstudiante(
                criteria.evaluacionJuradoId(), criteria.estudianteId());
    }
}
