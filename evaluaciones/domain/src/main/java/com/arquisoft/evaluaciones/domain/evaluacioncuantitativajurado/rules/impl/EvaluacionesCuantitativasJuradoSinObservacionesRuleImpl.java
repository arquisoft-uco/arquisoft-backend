package com.arquisoft.evaluaciones.domain.evaluacioncuantitativajurado.rules.impl;

import com.arquisoft.evaluaciones.domain.evaluacioncuantitativajurado.exception.EvaluacionesCuantitativasJuradoConObservacionesException;
import com.arquisoft.evaluaciones.domain.evaluacioncuantitativajurado.model.ObservacionesEvaluacionesCuantitativasJurado;
import com.arquisoft.evaluaciones.domain.evaluacioncuantitativajurado.rules.EvaluacionesCuantitativasJuradoSinObservacionesRule;

public class EvaluacionesCuantitativasJuradoSinObservacionesRuleImpl
        implements EvaluacionesCuantitativasJuradoSinObservacionesRule {

    @Override
    public void validar(ObservacionesEvaluacionesCuantitativasJurado observaciones) {
        if (observaciones.existenObservaciones()) {
            throw new EvaluacionesCuantitativasJuradoConObservacionesException(
                    observaciones.evaluaciones(), observaciones.evaluacionJurado());
        }
    }
}
